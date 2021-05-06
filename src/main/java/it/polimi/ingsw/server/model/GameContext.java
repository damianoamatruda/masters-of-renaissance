package it.polimi.ingsw.server.model;

import it.polimi.ingsw.common.ModelObservable;
import it.polimi.ingsw.common.ReducedProductionRequest;
import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.events.*;
import it.polimi.ingsw.server.model.cardrequirements.CardRequirementsNotMetException;
import it.polimi.ingsw.server.model.leadercards.LeaderCard;
import it.polimi.ingsw.server.model.resourcecontainers.IllegalResourceTransferException;
import it.polimi.ingsw.server.model.resourcecontainers.ResourceContainer;
import it.polimi.ingsw.server.model.resourcecontainers.Shelf;
import it.polimi.ingsw.server.model.resourcetypes.ResourceType;

import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

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
    public GameContext(Game game, GameFactory gameFactory) {
        this.game = game;
        this.setupDone = false;
        this.turnDone = false;
        this.gameFactory = gameFactory;
    }

    /**
     * Choose leaders from the hand of a player.
     *
     * @param v the view the action originates from.
     *          Used to send back errors
     * @param nickname the player
     * @param leaderIndexes the leader cards to choose
     * @throws IllegalActionException if the player cannot choose the leaders in the current state
     */
    public void chooseLeaders(View v, String nickname, List<Integer> leaderIndexes) {
        if (setupDone)
            notify(v, new ErrAction(new IllegalActionException("Choose leaders", "Setup already done.").getMessage()));
        
        Player player = getPlayerByNickname(nickname);
        List<LeaderCard> leaders = leaderIndexes.stream().map(l -> player.getLeaders().get(l)).toList();
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
     * @throws IllegalActionException if the player cannot choose initial resources in the current state
     */
    public void chooseResources(View v, String nickname, Map<Integer, Map<String, Integer>> reducedShelves) {
        if (setupDone)
            notify(v, new ErrAction(new IllegalActionException("Choose resources", "Setup already done.").getMessage()));
        
        Map<Shelf, Map<ResourceType, Integer>> shelves = new HashMap<>();
        reducedShelves.forEach((key, value) -> shelves.put((Shelf) game.getShelfById(key).orElseThrow(), translateResources(value)));
        Player current = getPlayerByNickname(nickname);
        try {
            current.chooseResources(game, shelves);
        } catch (CannotChooseException | IllegalProductionActivationException e) {
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
     * @throws IllegalActionException if the player cannot swap the contents of two shelves in the current state
     */
    public void swapShelves(View v, String nickname, Integer shelfId1, Integer shelfId2) {
        if (!preliminaryChecks(v, "Swap shelves")) return;

        Player player = getPlayerByNickname(nickname);;
        
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
     * @throws IllegalActionException if the player cannot activate a leader in the current state
     */
    public void activateLeader(View v, String nickname, Integer leaderid) {
        if (!preliminaryChecks(v, "Activate leader")) return;

        Player player = getPlayerByNickname(nickname);;
        
        if(!checkCurrentPlayer(v, player, "Activate leader")) return;

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
     * @throws IllegalActionException if the player cannot discard a leader in the current state
     */
    public void discardLeader(View v, String nickname, Integer leaderid) {
        if (!preliminaryChecks(v, "Discard leader")) return;

        Player player = getPlayerByNickname(nickname);;
        
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
     * @throws IllegalActionException if the player cannot take resources from the market in the current state
     */
    public void takeMarketResources(View v, String nickname, boolean isRow, int index,
                                    Map<ResourceType, Integer> replacements,
                                    Map<Integer, Map<String, Integer>> reducedShelves) {
        if (!preliminaryChecks(v, "Take market resources")) return;

        Player player = getPlayerByNickname(nickname);;
        
        if (!checkCurrentPlayer(v, player, "Take market resources")) return;

        Map<Shelf, Map<ResourceType, Integer>> shelves = new HashMap<>();
        reducedShelves.forEach((key, value) -> shelves.put((Shelf) game.getShelfById(key).orElseThrow(), translateResources(value)));
        try {
            game.getMarket().takeResources(game, player, isRow, index, replacements, shelves);
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
     * @throws IllegalActionException if the player cannot buy a development card in the current state
     */
    public void buyDevCard(View v, String nickname, String color, int level, int slotIndex,
                           Map<Integer, Map<String, Integer>> reducedResContainers) {
        if (!preliminaryChecks(v, "Buy development card")) return;

        Player player = getPlayerByNickname(nickname);;
        
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
     * @param v the view the action originates from.
     *          Used to send back errors
     * @param nickname          the player
     * @param reducedProdGroup the group of requested contemporary productions
     * @throws IllegalActionException if the player cannot activate a group of productions in the current state
     */
    public void activateProductionGroup(View v, String nickname, List<ReducedProductionRequest> reducedProdGroup) {
        if (!preliminaryChecks(v, "Activate production")) return;

        Player player = getPlayerByNickname(nickname);;
        
        if (!checkCurrentPlayer(v, player, "Activate production")) return;
        
        ProductionGroup productionGroup = new ProductionGroup(reducedProdGroup.stream().map (this::translateToProductionRequest).toList());
        try {
            productionGroup.activate(game, player);
        } catch (IllegalProductionActivationException e) {
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
     * @throws IllegalActionException if the player cannot end the turn in the current state
     */
    public void endTurn(View v, String nickname) {
        if (!setupDone || !turnDone)
            notify(v, new ErrAction(
                new IllegalActionException("End turn", !setupDone ? "Setup is not done yet" : "No action executed in the turn").getMessage()));
        
        Player player = getPlayerByNickname(nickname);;
        
        if (!checkCurrentPlayer(v, player, "End turn")) return;
        
        try {
            game.onTurnEnd();
        } catch (NoActivePlayersException e) {
            notify(v, new ErrAction(e.getMessage())); // TODO does this make sense?
        }
        turnDone = false;
    }

    /**
     * Returns the player who won the game, if the winner is a player.
     *
     * @return the player who won
     * @throws IllegalActionException if the player who won cannot be requested in the current state
     */
    // public Optional<String> getWinnerPlayer() {
    //     if (!game.hasEnded())
    //         notify(null, new ErrAction(new IllegalActionException("Get winner player", "Game has not ended yet").getMessage()));
    //     return game.getWinnerPlayer().map(Player::getNickname);
    // } TODO is this relevant?

    /**
     * Returns whether Lorenzo has won the game or not.
     *
     * @return <code>true</code> if Lorenzo is the winner of the game; <code>false</code> otherwise.
     * @throws IllegalActionException if this cannot be requested in the current state
     */
    // public boolean isBlackWinner() throws IllegalActionException {
    //     if (!game.hasEnded())
    //         throw new IllegalActionException();
    //     return game.isBlackWinner();
    // } TODO is this relevant?

    /**
     * Check if the last necessary setup move has been made.
     */
    private void checkEndSetup() {
        if (game.getPlayers().stream().allMatch(p -> p.hasChosenLeaders() && p.hasChosenResources()))
            setupDone = true;
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

    private ProductionGroup.ProductionRequest translateToProductionRequest(ReducedProductionRequest r) {
        Map<ResourceContainer, Map<ResourceType, Integer>> inputContainers = new HashMap<>();
        r.getInputContainers().forEach((key, value) -> inputContainers.put(game.getShelfById(key).orElseThrow(), translateResources(value)));
        Map<ResourceContainer, Map<ResourceType, Integer>> outputContainers = new HashMap<>();
        r.getInputContainers().forEach((key, value) -> outputContainers.put(game.getShelfById(key).orElseThrow(), translateResources(value)));
        return new ProductionGroup.ProductionRequest(game.getProductionById(r.getProductionId()).orElseThrow(), translateResources(r.getInputBlanksRep()), translateResources(r.getOutputBlanksRep()),
                Map.of(), inputContainers, outputContainers);
    }
}
