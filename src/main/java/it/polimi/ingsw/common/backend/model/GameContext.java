package it.polimi.ingsw.common.backend.model;

import it.polimi.ingsw.common.ModelObservable;
import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.backend.model.cardrequirements.CardRequirementsNotMetException;
import it.polimi.ingsw.common.backend.model.leadercards.LeaderCard;
import it.polimi.ingsw.common.backend.model.resourcecontainers.IllegalResourceTransferException;
import it.polimi.ingsw.common.backend.model.resourcecontainers.ResourceContainer;
import it.polimi.ingsw.common.backend.model.resourcecontainers.Shelf;
import it.polimi.ingsw.common.backend.model.resourcecontainers.Strongbox;
import it.polimi.ingsw.common.backend.model.resourcetransactions.*;
import it.polimi.ingsw.common.backend.model.resourcetypes.ResourceType;
import it.polimi.ingsw.common.events.ErrAction;
import it.polimi.ingsw.common.events.UpdateSetupDone;
import it.polimi.ingsw.common.reducedmodel.ReducedProductionRequest;

import java.util.*;

/**
 * This class manages the states and actions of a game.
 */
public class GameContext extends ModelObservable {
    /** The game. */
    private final Game game;

    private final GameFactory gameFactory;

    /** This represents the setup state of a game. */
    private boolean setupDone;

    /** This represents the state of a game during the turn of a player that has not made the mandatory move yet. */
    private boolean turnDone;

    /**
     * Initializes a context for the given game.
     *
     * @param game the game
     */
    public GameContext(Game game, GameFactory gameFactory, List<View> observers) {
        super(observers);
        this.game = game;
        this.setupDone = false;
        this.turnDone = false;
        this.gameFactory = gameFactory;
    }

    /**
     * Choose leaders from the hand of a player.
     *
     * @param v         the view the action originates from. Used to send back errors
     * @param nickname  the player
     * @param leaderIds the leader cards to choose
     */
    public void chooseLeaders(View v, String nickname, List<Integer> leaderIds) {
        if (setupDone) {
            notify(v, new ErrAction(new IllegalActionException("Choose leaders", "Setup already done.").getMessage()));
            return;
        }

        Player player = getPlayerByNickname(nickname);

        if (leaderIds.stream().map(player::getLeaderById).anyMatch(Optional::isEmpty)) {
            notify(v, new ErrAction("Leader not owned.")); // TODO: This is a PoC exception
            // TODO: Specify leader that is not owned
            return;
        }

        List<LeaderCard> leaders = leaderIds.stream().map(game::getLeaderById).filter(Optional::isPresent).map(Optional::get).toList();

        try {
            player.chooseLeaders(leaders);
        } catch (CannotChooseException e) {
            notify(v, new ErrAction(e.getMessage()));
            return;
        }

        checkEndSetup();
    }

    /**
     * Chooses the initial resources to take as a player.
     *
     * @param v              the view the action originates from. Used to send back errors
     * @param nickname       the player
     * @param reducedShelves the destination shelves
     */
    public void chooseResources(View v, String nickname, Map<Integer, Map<String, Integer>> reducedShelves) {
        if (setupDone) {
            notify(v, new ErrAction(new IllegalActionException("Choose resources", "Setup already done.").getMessage()));
            return;
        }

        Player player = getPlayerByNickname(nickname);

        // TODO: Use stream to do this
        Map<Shelf, Map<ResourceType, Integer>> shelves = new HashMap<>();
        reducedShelves.forEach((id, resMap) -> {
            try {
                shelves.put(player.getShelfById(id).orElseThrow(() -> new Exception("Shelf not owned.")), translateResMap(resMap)); // TODO: This is a PoC exception
            } catch (Exception e) {
                notify(v, new ErrAction(e.getMessage()));
            }
        });

        try {
            player.chooseResources(game, shelves);
        } catch (CannotChooseException | IllegalResourceTransactionActivationException e) {
            notify(v, new ErrAction(e.getMessage()));
            return;
        }

        checkEndSetup();
    }

    /**
     * Swaps the content of two shelves of a player.
     *
     * @param v        the view the action originates from. Used to send back errors
     * @param nickname the player
     * @param shelfId1 the first shelf
     * @param shelfId2 the second shelf
     */
    public void swapShelves(View v, String nickname, Integer shelfId1, Integer shelfId2) {
        if (!preliminaryChecks(v, "Swap shelves"))
            return;

        Player player = getPlayerByNickname(nickname);

        if (!checkCurrentPlayer(v, player, "Swap shelves"))
            return;

        Shelf s1;
        Shelf s2;

        try {
            s1 = player.getShelfById(shelfId1).orElseThrow(() -> new Exception("Shelf 1 not owned.")); // TODO: This is a PoC exception
            s2 = player.getShelfById(shelfId2).orElseThrow(() -> new Exception("Shelf 2 not owned.")); // TODO: This is a PoC exception
        } catch (Exception e) {
            notify(v, new ErrAction(e.getMessage()));
            return;
        }

        try {
            Shelf.swap(s1, s2);
        } catch (IllegalResourceTransferException e) {
            notify(v, new ErrAction(e.getMessage()));
        }
    }

    /**
     * Makes a player activate a leader card.
     *
     * @param v        the view the action originates from. Used to send back errors
     * @param nickname the player
     * @param leaderid the leader card to activate
     */
    public void activateLeader(View v, String nickname, Integer leaderid) {
        if (!preliminaryChecks(v, "Activate leader"))
            return;

        Player player = getPlayerByNickname(nickname);

        if (!checkCurrentPlayer(v, player, "Activate leader"))
            return;

        LeaderCard leader;

        try {
            leader = player.getLeaderById(leaderid).orElseThrow(() -> new Exception("Leader not owned.")); // TODO: This is a PoC exception
        } catch (Exception e) {
            notify(v, new ErrAction(e.getMessage()));
            return;
        }

        try {
            leader.activate(player);
        } catch (CardRequirementsNotMetException | IllegalArgumentException e) {
            notify(v, new ErrAction(e.getMessage()));
        }
    }

    /**
     * Makes a player discard a leader card.
     *
     * @param v        the view the action originates from. Used to send back errors
     * @param nickname the player
     * @param leaderid the leader card to discard
     */
    public void discardLeader(View v, String nickname, Integer leaderid) {
        if (!preliminaryChecks(v, "Discard leader"))
            return;

        Player player = getPlayerByNickname(nickname);

        if (!checkCurrentPlayer(v, player, "Discard leader"))
            return;

        LeaderCard leader = game.getLeaderById(leaderid).orElseThrow();
        try {
            player.discardLeader(game, leader);
        } catch (IndexOutOfBoundsException | ActiveLeaderDiscardException e) {
            notify(v, new ErrAction(e.getMessage()));
        }
    }

    /**
     * Takes resources from the market as a player.
     *
     * @param v              the view the action originates from. Used to send back errors
     * @param nickname       the player
     * @param isRow          <code>true</code> if a row is selected; <code>false</code> if a column is selected.
     * @param index          index of the selected row or column
     * @param replacements   a map of the chosen resources to take, if choices are applicable
     * @param reducedShelves a map of the shelves where to add the taken resources, if possible
     */
    public void takeMarketResources(View v, String nickname, boolean isRow, int index,
                                    Map<String, Integer> replacements,
                                    Map<Integer, Map<String, Integer>> reducedShelves) {
        if (!preliminaryChecks(v, "Take market resources"))
            return;

        Player player = getPlayerByNickname(nickname);

        if (!checkCurrentPlayer(v, player, "Take market resources"))
            return;

        // TODO: Use stream to do this
        Map<Shelf, Map<ResourceType, Integer>> shelves = new HashMap<>();
        reducedShelves.forEach((id, resMap) -> {
            try {
                shelves.put(player.getShelfById(id).orElseThrow(() -> new Exception("Shelf not owned.")), translateResMap(resMap)); // TODO: This is a PoC exception
            } catch (Exception e) {
                notify(v, new ErrAction(e.getMessage()));
            }
        });

        try {
            game.getMarket().takeResources(game, player, isRow, index, translateResMap(replacements), shelves);
        } catch (IllegalMarketTransferException e) {
            notify(v, new ErrAction(e.getMessage()));
            return;
        }

        turnDone = true;
    }

    /**
     * Makes a player buy a development card from the development card grid.
     *
     * @param v                    the view the action originates from. Used to send back errors
     * @param nickname             the player
     * @param color                the color of the card to be bought
     * @param level                the level of the card to be bought
     * @param slotIndex            the index of the dev slot where to put the development card
     * @param reducedResContainers a map of the resource containers where to take the storable resources
     */
    public void buyDevCard(View v, String nickname, String color, int level, int slotIndex,
                           Map<Integer, Map<String, Integer>> reducedResContainers) {
        if (!preliminaryChecks(v, "Buy development card"))
            return;

        Player player = getPlayerByNickname(nickname);

        if (!checkCurrentPlayer(v, player, "Buy development card"))
            return;

        // TODO: Use stream to do this
        Map<ResourceContainer, Map<ResourceType, Integer>> resContainers = new HashMap<>();
        reducedResContainers.forEach((id, resMap) -> {
            try {
                resContainers.put(player.getResourceContainerById(id).orElseThrow(() -> new Exception("Resource container not owned.")), translateResMap(resMap)); // TODO: This is a PoC exception
            } catch (Exception e) {
                notify(v, new ErrAction(e.getMessage()));
            }
        });

        try {
            game.getDevCardGrid().buyDevCard(game, player, gameFactory.getDevCardColor(color).orElseThrow(), level, slotIndex, resContainers);
        } catch (IllegalCardDepositException | CardRequirementsNotMetException | EmptyStackException e) {
            notify(v, new ErrAction(e.getMessage()));
            return;
        }

        turnDone = true;
    }

    /**
     * Makes a player activate a group of productions.
     *
     * @param v                   the view the action originates from. Used to send back errors
     * @param nickname            the player
     * @param reducedProdRequests the group of requested contemporary productions
     */
    public void activateProductionRequests(View v, String nickname, List<ReducedProductionRequest> reducedProdRequests) {
        if (!preliminaryChecks(v, "Activate production"))
            return;

        Player player = getPlayerByNickname(nickname);

        if (!checkCurrentPlayer(v, player, "Activate production"))
            return;

        if (reducedProdRequests.stream().map(ReducedProductionRequest::getProductionId).map(player::getProductionById).anyMatch(Optional::isEmpty)) {
            notify(v, new ErrAction("Production not owned.")); // TODO: This is a PoC exception
            // TODO: Specify production that is not owned
            return;
        }

        List<ProductionRequest> prodRequests;

        try {
            prodRequests = reducedProdRequests.stream().map(r -> translateToProductionRequest(player, r)).toList();
        } catch (IllegalResourceTransactionReplacementsException | IllegalResourceTransactionContainersException e) {
            notify(v, new ErrAction(e.getMessage()));
            return;
        }

        ResourceTransaction transaction = new ResourceTransaction(List.copyOf(prodRequests));
        try {
            transaction.activate(game, player);
        } catch (IllegalResourceTransactionActivationException e) {
            notify(v, new ErrAction(e.getMessage()));
            return;
        }

        turnDone = true;
    }

    /**
     * Makes a player end his turn.
     *
     * @param v        the view the action originates from. Used to send back errors
     * @param nickname the player
     */
    public void endTurn(View v, String nickname) {
        if (!setupDone || !turnDone) {
            notify(v, new ErrAction(
                    new IllegalActionException("End turn", !setupDone ? "Setup is not done yet" : "No action executed in the turn").getMessage()));
            return;
        }

        Player player = getPlayerByNickname(nickname);

        if (!checkCurrentPlayer(v, player, "End turn"))
            return;

        try {
            game.onTurnEnd();
        } catch (NoActivePlayersException e) {
            notify(v, new ErrAction(e.getMessage())); // TODO does this make sense?
            return;
        }

        turnDone = false;
    }

    /**
     * Check if the last necessary setup move has been made.
     */
    private void checkEndSetup() {
        if (game.getPlayers().stream().allMatch(p -> p.hasChosenLeaders() && p.hasChosenResources())) {
            setupDone = true;
            notifyBroadcast(new UpdateSetupDone());
        }
    }

    /**
     * Checks if the given player is the current player in the turn.
     *
     * @param v      the view to send the error massage back with
     * @param player the player to check
     * @param action the action trying to be performed
     * @return whether the ckeck passed
     */
    private boolean checkCurrentPlayer(View v, Player player, String action) {
        if (!player.equals(game.getCurrentPlayer())) {
            notify(v, new ErrAction(new IllegalActionException(action, "You are not the current player in the turn.").getMessage()));
            return false;
        }
        return true;
    }

    private Player getPlayerByNickname(String nickname) throws NoSuchElementException {
        return game.getPlayers().stream().filter(p -> p.getNickname().equals(nickname)).findFirst().orElseThrow();
    }

    /**
     * Checks whether the setup still isn't finished or the game has already ended. Action methods can use these checks
     * to confirm their legitimacy of execution, before starting.
     *
     * @param v      the view to use to send back the error, if the checks don't pass
     * @param action the action trying to be performed. Will be used as a reason when sending the error message to the
     *               client trying to execute the action.
     * @return whether the checks passed
     */
    private boolean preliminaryChecks(View v, String action) {
        if (!setupDone || game.hasEnded()) {
            notify(v, new ErrAction(new IllegalActionException(action, !setupDone ? "Setup phase not concluded." : "Game has already ended").getMessage()));
            return false;
        }
        return true;
    }

    private Map<ResourceType, Integer> translateResMap(Map<String, Integer> r) {
        // TODO: Use stream
        Map<ResourceType, Integer> res = new HashMap<>();
        r.forEach((resTypeName, quantity) -> res.put(gameFactory.getResourceType(resTypeName).orElseThrow(), quantity));
        return res;
    }

    private ProductionRequest translateToProductionRequest(Player player, ReducedProductionRequest r) throws IllegalResourceTransactionReplacementsException, IllegalResourceTransactionContainersException {
        Map<ResourceContainer, Map<ResourceType, Integer>> inputContainers = new HashMap<>();
        r.getInputContainers().forEach((id, resMap) -> inputContainers.put(player.getResourceContainerById(id).orElseThrow(), translateResMap(resMap)));
        Map<Strongbox, Map<ResourceType, Integer>> outputStrongboxes = new HashMap<>();
        r.getInputContainers().forEach((id, resMap) -> outputStrongboxes.put(player.getStrongboxById(id).orElseThrow(), translateResMap(resMap)));
        return new ProductionRequest(game.getProductionById(r.getProductionId()).orElseThrow(), translateResMap(r.getInputBlanksRep()), translateResMap(r.getOutputBlanksRep()),
                inputContainers, outputStrongboxes);
    }
}
