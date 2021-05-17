package it.polimi.ingsw.common.backend.model;

import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.backend.model.cardrequirements.CardRequirementsNotMetException;
import it.polimi.ingsw.common.backend.model.leadercards.LeaderCard;
import it.polimi.ingsw.common.backend.model.resourcecontainers.IllegalResourceTransferException;
import it.polimi.ingsw.common.backend.model.resourcecontainers.ResourceContainer;
import it.polimi.ingsw.common.backend.model.resourcecontainers.Shelf;
import it.polimi.ingsw.common.backend.model.resourcetransactions.*;
import it.polimi.ingsw.common.backend.model.resourcetypes.ResourceType;
import it.polimi.ingsw.common.events.mvevents.ErrAction;
import it.polimi.ingsw.common.reducedmodel.ReducedProductionRequest;

import java.util.*;

/**
 * This class manages the states and actions of a game.
 */
public class GameContext {
    /** The game. */
    private final Game game;

    /** The game factory. */
    private final GameFactory gameFactory;

    /** This represents the state of a game during the turn of a player that has not made the mandatory move yet. */
    private boolean mandatoryActionDone;

    /**
     * Initializes a context for the given game.
     *
     * @param game the game
     */
    public GameContext(Game game, GameFactory gameFactory) {
        this.game = game;
        this.gameFactory = gameFactory;
        this.mandatoryActionDone = false;
    }

    public void registerViewToModel(View view, String nickname) {
        view.registerToModelGame(game);
        view.registerToModelPlayer(getPlayerByNickname(nickname));
    }

    public void unregisterViewToModel(View view, String nickname) {
        view.unregisterToModelGame(game);
        view.unregisterToModelPlayer(getPlayerByNickname(nickname));
    }

    public void start() {
        game.dispatchInitialState();
        game.getMarket().dispatchInitialState();
        game.getDevCardGrid().dispatchInitialState();
        game.getPlayers().forEach(Player::dispatchInitialState);
        game.getPlayers().forEach(player -> player.getSetup().giveInitialFaithPoints(game, player));
    }

    public void resume(View view) {
        game.dispatchResumeState(view);
        game.getPlayers().forEach(p -> p.dispatchResumeState(view));
    }

    /**
     * Choose leaders from the hand of a player.
     *
     * @param view      the view the action originates from. Used to on back errors
     * @param nickname  the player
     * @param leaderIds the leader cards to choose
     */
    public void chooseLeaders(View view, String nickname, List<Integer> leaderIds) {
        Player player = getPlayerByNickname(nickname);

        if (player.getSetup().isDone()) {
            view.on(new ErrAction(new IllegalActionException("Choose leaders", "Setup already done.")));
            return;
        }

        if (leaderIds.stream().map(player::getLeaderById).anyMatch(Optional::isEmpty)) {
            view.on(new ErrAction("Leader not owned.")); // TODO: This is a PoC exception
            // TODO: Specify leader that is not owned
            /*throw new CannotChooseException(
                    String.format("Cannot choose leader %d: not in your hand.",
                            leaders.stream()
                                    .map(Card::getId)
                                    .filter(id -> !chosenLeaders.stream().map(Card::getId).toList().contains(id))
                                    .findAny().orElse(-1))); */
            return;
        }

        List<LeaderCard> leaders = leaderIds.stream().map(game::getLeaderById).filter(Optional::isPresent).map(Optional::get).toList();

        try {
            player.getSetup().chooseLeaders(game, player, leaders);
        } catch (CannotChooseException e) {
            view.on(new ErrAction(e));
        }
    }

    /**
     * Chooses the initial resources to take as a player.
     *
     * @param view              the view the action originates from. Used to on back errors
     * @param nickname       the player
     * @param reducedShelves the destination shelves
     */
    public void chooseResources(View view, String nickname, Map<Integer, Map<String, Integer>> reducedShelves) {
        Player player = getPlayerByNickname(nickname);

        if (player.getSetup().isDone()) {
            view.on(new ErrAction(new IllegalActionException("Choose resources", "Setup already done.")));
            return;
        }

        // TODO: Use stream to do this
        Map<Shelf, Map<ResourceType, Integer>> shelves = new HashMap<>();
        reducedShelves.forEach((id, resMap) -> {
            try {
                shelves.put(player.getShelfById(id).orElseThrow(() -> new Exception("Shelf not owned.")), translateResMap(resMap)); // TODO: This is a PoC exception
            } catch (Exception e) {
                view.on(new ErrAction(e));
            }
        });

        try {
            player.getSetup().chooseResources(game, player, shelves);
        } catch (CannotChooseException | IllegalResourceTransactionActivationException e) {
            view.on(new ErrAction(e));
        }
    }

    /**
     * Swaps the content of two shelves of a player.
     *
     * @param view        the view the action originates from. Used to on back errors
     * @param nickname the player
     * @param shelfId1 the first shelf
     * @param shelfId2 the second shelf
     */
    public void swapShelves(View view, String nickname, Integer shelfId1, Integer shelfId2) {
        Player player = getPlayerByNickname(nickname);

        if (!preliminaryChecks(view, player, "Swap shelves"))
            return;

        if (!checkCurrentPlayer(view, player, "Swap shelves"))
            return;

        Shelf shelf1;
        Shelf shelf2;

        try {
            shelf1 = player.getShelfById(shelfId1).orElseThrow(() -> new Exception("Shelf 1 not owned.")); // TODO: This is a PoC exception
            shelf2 = player.getShelfById(shelfId2).orElseThrow(() -> new Exception("Shelf 2 not owned.")); // TODO: This is a PoC exception
        } catch (Exception e) {
            view.on(new ErrAction(e));
            return;
        }

        try {
            Shelf.swap(shelf1, shelf2);
        } catch (IllegalResourceTransferException e) {
            view.on(new ErrAction(e));
        }
    }

    /**
     * Makes a player activate a leader card.
     *
     * @param view        the view the action originates from. Used to on back errors
     * @param nickname the player
     * @param leaderId the leader card to activate
     */
    public void activateLeader(View view, String nickname, Integer leaderId) {
        Player player = getPlayerByNickname(nickname);

        if (!preliminaryChecks(view, player, "Activate leader"))
            return;

        if (!checkCurrentPlayer(view, player, "Activate leader"))
            return;

        LeaderCard leader;

        try {
            leader = player.getLeaderById(leaderId).orElseThrow(() -> new Exception("Leader not owned.")); // TODO: This is a PoC exception
        } catch (Exception e) {
            view.on(new ErrAction(e));
            return;
        }

        try {
            leader.activate(player);
        } catch (CardRequirementsNotMetException | IllegalArgumentException e) {
            view.on(new ErrAction(e));
        }
    }

    /**
     * Makes a player discard a leader card.
     *
     * @param view        the view the action originates from. Used to on back errors
     * @param nickname the player
     * @param leaderId the leader card to discard
     */
    public void discardLeader(View view, String nickname, Integer leaderId) {
        Player player = getPlayerByNickname(nickname);

        if (!preliminaryChecks(view, player, "Discard leader"))
            return;

        if (!checkCurrentPlayer(view, player, "Discard leader"))
            return;

        LeaderCard leader;
        
        try {
            leader = player.getLeaderById(leaderId).orElseThrow(() -> new Exception("Leader not owned.")); // TODO: This is a PoC exception
        } catch (Exception e) {
            view.on(new ErrAction(e));
            return;
        }

        try {
            player.discardLeader(game, leader);
        } catch (IndexOutOfBoundsException | ActiveLeaderDiscardException e) {
            view.on(new ErrAction(e));
        }
    }

    /**
     * Takes resources from the market as a player.
     *
     * @param view              the view the action originates from. Used to on back errors
     * @param nickname       the player
     * @param isRow          <code>true</code> if a row is selected; <code>false</code> if a column is selected.
     * @param index          index of the selected row or column
     * @param replacements   a map of the chosen resources to take, if choices are applicable
     * @param reducedShelves a map of the shelves where to add the taken resources, if possible
     */
    public void takeMarketResources(View view, String nickname, boolean isRow, int index,
                                    Map<String, Integer> replacements,
                                    Map<Integer, Map<String, Integer>> reducedShelves) {
        Player player = getPlayerByNickname(nickname);

        if (!preliminaryChecks(view, player, "Take market resources"))
            return;

        if (!checkCurrentPlayer(view, player, "Take market resources"))
            return;

        if (!checkMandatoryActionNotDone(view, "Take market resources"))
            return;

        // TODO: Use stream to do this
        Map<Shelf, Map<ResourceType, Integer>> shelves = new HashMap<>();
        reducedShelves.forEach((id, resMap) -> {
            try {
                shelves.put(player.getShelfById(id).orElseThrow(() -> new Exception("Shelf not owned.")), translateResMap(resMap)); // TODO: This is a PoC exception
            } catch (Exception e) {
                view.on(new ErrAction(e));
            }
        });

        try {
            game.getMarket().takeResources(game, player, isRow, index, translateResMap(replacements), shelves);
        } catch (IllegalMarketTransferException e) {
            view.on(new ErrAction(e));
            return;
        }

        mandatoryActionDone = true;
    }

    /**
     * Makes a player buy a development card from the development card grid.
     *
     * @param view                    the view the action originates from. Used to on back errors
     * @param nickname             the player
     * @param color                the color of the card to be bought
     * @param level                the level of the card to be bought
     * @param slotIndex            the index of the deview slot where to put the development card
     * @param reducedResContainers a map of the resource containers where to take the storable resources
     */
    public void buyDevCard(View view, String nickname, String color, int level, int slotIndex,
                           Map<Integer, Map<String, Integer>> reducedResContainers) {
        Player player = getPlayerByNickname(nickname);

        if (!preliminaryChecks(view, player, "Buy development card"))
            return;

        if (!checkCurrentPlayer(view, player, "Buy development card"))
            return;

        if (!checkMandatoryActionNotDone(view, "Buy development card"))
            return;

        // TODO: Use stream to do this
        Map<ResourceContainer, Map<ResourceType, Integer>> resContainers = new HashMap<>();
        reducedResContainers.forEach((id, resMap) -> {
            try {
                resContainers.put(player.getResourceContainerById(id).orElseThrow(() -> new Exception("Resource container not owned.")), translateResMap(resMap)); // TODO: This is a PoC exception
            } catch (Exception e) {
                // TODO: Specify resource container that is not owned
                view.on(new ErrAction(e));
                throw new RuntimeException();
            }
        });

        try {
            game.getDevCardGrid().buyDevCard(game, player, gameFactory.getDevCardColor(color).orElseThrow(), level, slotIndex, resContainers);
        } catch (IllegalCardDepositException | CardRequirementsNotMetException | EmptyStackException e) {
            view.on(new ErrAction(e));
            return;
        }

        mandatoryActionDone = true;
    }

    /**
     * Makes a player activate a group of productions.
     *
     * @param view                   the view the action originates from. Used to on back errors
     * @param nickname            the player
     * @param reducedProdRequests the group of requested contemporary productions
     */
    public void activateProductionRequests(View view, String nickname, List<ReducedProductionRequest> reducedProdRequests) {
        Player player = getPlayerByNickname(nickname);

        if (!preliminaryChecks(view, player, "Activate production"))
            return;

        if (!checkCurrentPlayer(view, player, "Activate production"))
            return;

        if (!checkMandatoryActionNotDone(view, "Activate production"))
            return;

        if (reducedProdRequests.stream().map(ReducedProductionRequest::getProduction).map(player::getProductionById).anyMatch(Optional::isEmpty)) {
            view.on(new ErrAction("Production not owned.")); // TODO: This is a PoC exception
            // TODO: Specify production that is not owned
            return;
        }

        List<ProductionRequest> prodRequests;

        try {
            prodRequests = reducedProdRequests.stream().map(r -> translateToProductionRequest(view, player, r)).toList();
        } catch (IllegalResourceTransactionReplacementsException | IllegalResourceTransactionContainersException e) {
            view.on(new ErrAction(e));
            return;
        }

        ResourceTransaction transaction = new ResourceTransaction(List.copyOf(prodRequests));
        try {
            transaction.activate(game, player);
        } catch (IllegalResourceTransactionActivationException e) {
            view.on(new ErrAction(e));
            return;
        }

        mandatoryActionDone = true;
    }

    /**
     * Makes a player end his turn.
     *
     * @param view        the view the action originates from. Used to on back errors
     * @param nickname the player
     */
    public void endTurn(View view, String nickname) {
        Player player = getPlayerByNickname(nickname);

        if (!preliminaryChecks(view, player, "End turn"))
            return;

        if (!checkCurrentPlayer(view, player, "End turn"))
            return;

        if (!mandatoryActionDone) {
            view.on(new ErrAction(new IllegalActionException("End turn", "No mandatory action done in the turn")));
            return;
        }

        try {
            game.onTurnEnd();
        } catch (NoActivePlayersException e) {
            view.on(new ErrAction(e)); // TODO does this make sense?
            return;
        }

        mandatoryActionDone = false;
    }

    public void setActive(String nickname, boolean active) throws NoActivePlayersException {
        Player player = getPlayerByNickname(nickname);
        player.setActive(active);
        if (!active && player.equals(game.getCurrentPlayer()))
            game.onTurnEnd();
    }

    /**
     * Ensures that the setup still isn't finished and the game has not ended yet. Action methods can use these checks
     * to confirm their legitimacy of execution, before starting.
     *
     * @param view   the view to use to on back the error, if the checks don't on
     * @param action the action trying to be performed. Will be used as a reason when sending the error message to the
     *               client trying to execute the action.
     * @return whether the checks passed
     */
    private boolean preliminaryChecks(View view, Player player, String action) {
        if (!player.getSetup().isDone() || game.hasEnded()) {
            view.on(new ErrAction(new IllegalActionException(action, !player.getSetup().isDone() ? "Setup phase not concluded." : "Game has already ended")));
            return false;
        }
        return true;
    }

    /**
     * Ensures that the given player is the current player in the turn.
     *
     * @param view      the view to on the error massage back with
     * @param player the player to check
     * @param action the action trying to be performed
     * @return whether the check passed
     */
    private boolean checkCurrentPlayer(View view, Player player, String action) {
        if (!player.equals(game.getCurrentPlayer())) {
            view.on(new ErrAction(new IllegalActionException(action, "You are not the current player in the turn.")));
            return false;
        }
        return true;
    }

    /**
     * Ensures that the current player has not done a mandatory action yet.
     *
     * @param view      the view to on the error massage back with
     * @param action the action trying to be performed
     * @return whether the check passed
     */
    private boolean checkMandatoryActionNotDone(View view, String action) {
        if (mandatoryActionDone) {
            view.on(new ErrAction(new IllegalActionException(action, "You have already done a mandatory action.")));
            return false;
        }
        return true;
    }

    private Player getPlayerByNickname(String nickname) throws NoSuchElementException {
        return game.getPlayers().stream().filter(p -> p.getNickname().equals(nickname)).findAny().orElseThrow();
    }

    private Map<ResourceType, Integer> translateResMap(Map<String, Integer> r) {
        // TODO: Use stream
        Map<ResourceType, Integer> res = new HashMap<>();
        r.forEach((resTypeName, quantity) -> res.put(gameFactory.getResourceType(resTypeName).orElseThrow(), quantity));
        return res;
    }

    private ProductionRequest translateToProductionRequest(View view, Player player, ReducedProductionRequest r) throws IllegalResourceTransactionReplacementsException, IllegalResourceTransactionContainersException {
        Map<ResourceContainer, Map<ResourceType, Integer>> inputContainers = new HashMap<>();
        r.getInputContainers().forEach((id, resMap) -> {
            try {
                inputContainers.put(player.getResourceContainerById(id).orElseThrow(() -> new Exception("Resource container not owned.")), translateResMap(resMap)); // TODO: This is a PoC exception
            } catch (Exception e) {
                view.on(new ErrAction(e));
                // TODO: Specify resource container that is not owned
                throw new RuntimeException();
            }
        });
        return new ProductionRequest(game.getProductionById(r.getProduction()).orElseThrow(), translateResMap(r.getInputBlanksRep()), translateResMap(r.getOutputBlanksRep()),
                inputContainers, player.getStrongbox());
    }
}
