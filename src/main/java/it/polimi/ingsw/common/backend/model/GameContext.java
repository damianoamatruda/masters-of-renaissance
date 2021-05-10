package it.polimi.ingsw.common.backend.model;

import it.polimi.ingsw.common.ModelObservable;
import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.backend.model.cardrequirements.CardRequirementsNotMetException;
import it.polimi.ingsw.common.backend.model.leadercards.LeaderCard;
import it.polimi.ingsw.common.backend.model.resourcecontainers.IllegalResourceTransferException;
import it.polimi.ingsw.common.backend.model.resourcecontainers.ResourceContainer;
import it.polimi.ingsw.common.backend.model.resourcecontainers.Shelf;
import it.polimi.ingsw.common.backend.model.resourcecontainers.Strongbox;
import it.polimi.ingsw.common.backend.model.resourcetransactions.IllegalResourceTransactionActivationException;
import it.polimi.ingsw.common.backend.model.resourcetransactions.ProductionRequest;
import it.polimi.ingsw.common.backend.model.resourcetransactions.ResourceTransaction;
import it.polimi.ingsw.common.backend.model.resourcetypes.ResourceType;
import it.polimi.ingsw.common.events.ErrAction;
import it.polimi.ingsw.common.events.UpdateSetupDone;
import it.polimi.ingsw.common.reducedmodel.ReducedProductionRequest;

import java.util.*;
import java.util.stream.Stream;

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
        if (setupDone)
            notify(v, new ErrAction(new IllegalActionException("Choose leaders", "Setup already done.").getMessage()));

        Player player = getPlayerByNickname(nickname);
        Stream<Optional<LeaderCard>> leaderStream = leaderIds.stream().map(game::getLeaderById);
        if (leaderStream.anyMatch(Optional::isEmpty))
            notify(v, new ErrAction("Leader not found.")); // TODO: This is a PoC exception
        List<LeaderCard> leaders = leaderStream.filter(Optional::isPresent).map(Optional::get).toList();
        try {
            player.chooseLeaders(leaders);
        } catch (CannotChooseException e) {
            notify(v, new ErrAction(e.getMessage()));
        }
        checkEndSetup();
    }

    /**
     * Chooses the initial resources to take as a player.
     *
     * @param v the view the action originates from.
     *          Used to send back errors
     * @param nickname  the player
     * @param reducedShelves the destination shelves
     */
    public void chooseResources(View v, String nickname, Map<Integer, Map<String, Integer>> reducedShelves) {
        if (setupDone)
            notify(v, new ErrAction(new IllegalActionException("Choose resources", "Setup already done.").getMessage()));
        
        Map<Shelf, Map<ResourceType, Integer>> shelves = new HashMap<>();
        reducedShelves.forEach((key, value) -> shelves.put((Shelf) game.getShelfById(key).orElseThrow(), translateResources(value)));
        Player current = getPlayerByNickname(nickname);
        try {
            current.chooseResources(game, shelves);
        } catch (CannotChooseException | IllegalResourceTransactionActivationException e) {
            notify(v, new ErrAction(e.getMessage()));
        }
        checkEndSetup();
    }

    /**
     * Swaps the content of two shelves of a player.
     *
     * @param v the view the action originates from.
     *          Used to send back errors
     * @param nickname the player
     * @param shelfId1     the first shelf
     * @param shelfId2     the second shelf
     */
    public void swapShelves(View v, String nickname, Integer shelfId1, Integer shelfId2) {
        if (!preliminaryChecks(v, "Swap shelves")) return;

        Player player = getPlayerByNickname(nickname);

        if (!checkCurrentPlayer(v, player, "Swap shelves")) return;

        Shelf s1 = (Shelf) game.getShelfById(shelfId1).orElseThrow();
        Shelf s2 = (Shelf) game.getShelfById(shelfId2).orElseThrow();
        try {
            Shelf.swap(s1, s2);
        } catch (IllegalResourceTransferException e) {
            notify(v, new ErrAction(e.getMessage()));
        }
    }

    /**
     * Makes a player activate a leader card.
     *
     * @param v the view the action originates from.
     *          Used to send back errors
     * @param nickname the player
     * @param leaderid the leader card to activate
     */
    public void activateLeader(View v, String nickname, Integer leaderid) {
        if (!preliminaryChecks(v, "Activate leader")) return;

        Player player = getPlayerByNickname(nickname);

        if (!checkCurrentPlayer(v, player, "Activate leader")) return;

        LeaderCard leader = game.getLeaderById(leaderid).orElseThrow();
        try {
            leader.activate(player);
        } catch (CardRequirementsNotMetException | IllegalArgumentException e) {
            notify(v, new ErrAction(e.getMessage()));
        }
    }

    /**
     * Makes a player discard a leader card.
     *
     * @param v the view the action originates from.
     *          Used to send back errors
     * @param nickname the player
     * @param leaderid the leader card to discard
     */
    public void discardLeader(View v, String nickname, Integer leaderid) {
        if (!preliminaryChecks(v, "Discard leader")) return;

        Player player = getPlayerByNickname(nickname);

        if (!checkCurrentPlayer(v, player, "Discard leader")) return;

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
     * @param v the view the action originates from.
     *          Used to send back errors
     * @param nickname       the player
     * @param isRow        <code>true</code> if a row is selected; <code>false</code> if a column is selected.
     * @param index        index of the selected row or column
     * @param replacements a map of the chosen resources to take, if choices are applicable
     * @param reducedShelves      a map of the shelves where to add the taken resources, if possible
     */
    public void takeMarketResources(View v, String nickname, boolean isRow, int index,
                                    Map<String, Integer> replacements,
                                    Map<Integer, Map<String, Integer>> reducedShelves) {
        if (!preliminaryChecks(v, "Take market resources")) return;

        Player player = getPlayerByNickname(nickname);

        if (!checkCurrentPlayer(v, player, "Take market resources")) return;

        Map<Shelf, Map<ResourceType, Integer>> shelves = new HashMap<>();
        reducedShelves.forEach((key, value) -> shelves.put((Shelf) game.getShelfById(key).orElseThrow(), translateResources(value)));

        try {
            game.getMarket().takeResources(game, player, isRow, index, translateResources(replacements), shelves);
        } catch (IllegalMarketTransferException e) {
            notify(v, new ErrAction(e.getMessage()));
        }
        turnDone = true;
    }

    /**
     * Makes a player buy a development card from the development card grid.
     *
     * @param v the view the action originates from.
     *          Used to send back errors
     * @param nickname        the player
     * @param color         the color of the card to be bought
     * @param level         the level of the card to be bought
     * @param slotIndex     the index of the dev slot where to put the development card
     * @param reducedResContainers a map of the resource containers where to take the storable resources
     */
    public void buyDevCard(View v, String nickname, String color, int level, int slotIndex,
                           Map<Integer, Map<String, Integer>> reducedResContainers) {
        if (!preliminaryChecks(v, "Buy development card")) return;

        Player player = getPlayerByNickname(nickname);

        if (!checkCurrentPlayer(v, player, "Buy development card")) return;

        Map<ResourceContainer, Map<ResourceType, Integer>> resContainers = new HashMap<>();
        reducedResContainers.forEach((key, value) -> resContainers.put(game.getShelfById(key).orElseThrow(), translateResources(value)));

        try {
            game.getDevCardGrid().buyDevCard(game, player, gameFactory.getDevCardColor(color).orElseThrow(), level, slotIndex, resContainers);
        } catch (IllegalCardDepositException | CardRequirementsNotMetException | EmptyStackException e) {
            notify(v, new ErrAction(e.getMessage()));
        }
        turnDone = true;
    }

    /**
     * Makes a player activate a group of productions.
     *
     * @param v                the view the action originates from. Used to send back errors
     * @param nickname         the player
     * @param reducedProdGroup the group of requested contemporary productions
     */
    public void activateProductionRequests(View v, String nickname, List<ReducedProductionRequest> reducedProdGroup) {
        if (!preliminaryChecks(v, "Activate production")) return;

        Player player = getPlayerByNickname(nickname);

        if (!checkCurrentPlayer(v, player, "Activate production")) return;

        ResourceTransaction transaction = new ResourceTransaction(List.copyOf(reducedProdGroup.stream().map(this::translateToProductionRequest).toList()));
        try {
            transaction.activate(game, player);
        } catch (IllegalResourceTransactionActivationException e) {
            notify(v, new ErrAction(e.getMessage()));
        }
        turnDone = true;
    }

    /**
     * Makes a player end his turn.
     *
     * @param v the view the action originates from.
     *          Used to send back errors
     * @param nickname the player
     */
    public void endTurn(View v, String nickname) {
        if (!setupDone || !turnDone)
            notify(v, new ErrAction(
                    new IllegalActionException("End turn", !setupDone ? "Setup is not done yet" : "No action executed in the turn").getMessage()));

        Player player = getPlayerByNickname(nickname);

        if (!checkCurrentPlayer(v, player, "End turn")) return;

        try {
            game.onTurnEnd();
        } catch (NoActivePlayersException e) {
            notify(v, new ErrAction(e.getMessage())); // TODO does this make sense?
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
     * @param v the view to send the error massage back with
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
     * Checks whether the setup still isn't finished or the game has already ended.
     * Action methods can use these checks to confirm their legitimacy of execution, before starting.
     * 
     * @param v the view to use to send back the error, if the checks don't pass
     * @param action the action trying to be performed.
     *               Will be used as a reason when sending the error message to the client
     *               trying to execute the action.
     * @return whether the checks passed
     */
    private boolean preliminaryChecks(View v, String action) {
        if (!setupDone || game.hasEnded()) {
            notify(v, new ErrAction(new IllegalActionException(action, !setupDone ? "Setup phase not concluded." : "Game has already ended").getMessage()));
            return false;
        }
        return true;
    }

    private Map<ResourceType, Integer> translateResources(Map<String,Integer> r) {
        Map<ResourceType, Integer> res = new HashMap<>();
        r.forEach((key, value) -> res.put(gameFactory.getResourceType(key).orElseThrow(), value));
        return res;
    }

    private ProductionRequest translateToProductionRequest(ReducedProductionRequest r) {
        Map<ResourceContainer, Map<ResourceType, Integer>> inputContainers = new HashMap<>();
        r.getInputContainers().forEach((key, value) -> inputContainers.put(game.getShelfById(key).orElseThrow(), translateResources(value)));
        Map<Strongbox, Map<ResourceType, Integer>> outputContainers = new HashMap<>();
        // TODO: Do not cast to Strongbox
        r.getInputContainers().forEach((key, value) -> outputContainers.put((Strongbox) game.getShelfById(key).orElseThrow(), translateResources(value)));
        // TODO: This should check runtime validation exception in constructor
        return new ProductionRequest(game.getProductionById(r.getProductionId()).orElseThrow(), translateResources(r.getInputBlanksRep()), translateResources(r.getOutputBlanksRep()),
                inputContainers, outputContainers);
    }
}