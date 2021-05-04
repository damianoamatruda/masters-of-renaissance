package it.polimi.ingsw.server.model;

import it.polimi.ingsw.common.ReducedDevCardGrid;
import it.polimi.ingsw.common.ReducedMarket;
import it.polimi.ingsw.common.ReducedProductionRequest;
import it.polimi.ingsw.server.model.cardrequirements.CardRequirementsNotMetException;
import it.polimi.ingsw.server.model.leadercards.LeaderCard;
import it.polimi.ingsw.server.model.resourcecontainers.IllegalResourceTransferException;
import it.polimi.ingsw.server.model.resourcecontainers.ResourceContainer;
import it.polimi.ingsw.server.model.resourcecontainers.Shelf;
import it.polimi.ingsw.server.model.resourcetypes.ResourceType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * This class manages the states and actions of a game.
 */
public class GameContext {
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
     * Getter of all the players who joined the game at the beginning.
     *
     * @return the list of players (including who disconnected after)
     */
    public Optional<Player> getPlayer(String nickname) {
        return game.getPlayers().stream().filter(p -> p.getNickname().equals(nickname)).findFirst();
    }

    /**
     * Returns the leader cards.
     *
     * @return the list of leader cards
     * @throws IllegalActionException if the list of leader cards cannot be requested in the current state
     */
    public List<Integer> getLeaderCards() throws IllegalActionException {
        if (setupDone)
            throw new IllegalActionException();
        return game.getLeaderCards().stream().map(Card::getId).toList();
    }

    /**
     * Returns the development cards.
     *
     * @return the list of development cards
     * @throws IllegalActionException if the list of development cards cannot be requested in the current state
     */
    public List<Integer> getDevelopmentCards() throws IllegalActionException {
        if (setupDone)
            throw new IllegalActionException();
        return game.getDevelopmentCards().stream().map(Card::getId).toList();
    }

    /**
     * Getter of the resource containers used in the game.
     *
     * @return the list of resource containers
     */
    public List<Integer> getResContainers() throws IllegalActionException {
        if (setupDone)
            throw new IllegalActionException();
        return game.getResContainers().stream().map(ResourceContainer::getId).toList();
    }

    /**
     * Getter of the productions used in the game.
     *
     * @return the list of productions
     */
    public List<Integer> getProductions() throws IllegalActionException {
        if (setupDone)
            throw new IllegalActionException();
        return game.getProductions().stream().map(Production::getId).toList();
    }

    /**
     * Returns the game market.
     *
     * @return the market
     * @throws IllegalActionException if the game cannot be requested in the current state
     */
    public ReducedMarket getMarket() throws IllegalActionException {
        if (setupDone)
            throw new IllegalActionException();
        return new ReducedMarket(game.getMarket());
    }

    /**
     * Returns the game development card grid.
     *
     * @return the development card grid
     * @throws IllegalActionException if the game cannot be requested in the current state
     */
    public ReducedDevCardGrid getDevCardGrid() throws IllegalActionException {
        if (setupDone)
            throw new IllegalActionException();
        DevCardGrid grid = game.getDevCardGrid();
        return new ReducedDevCardGrid(grid);
    }

    /**
     * Choose leaders from the hand of a player.
     *
     * @param nick  the player
     * @param leaderIndexes the leader cards to choose
     * @throws IllegalActionException if the player cannot choose the leaders in the current state
     */
    public void chooseLeaders(String nick, List<Integer> leaderIndexes) throws IllegalActionException, CannotChooseException {
        if (setupDone)
            throw new IllegalActionException();
        Player player = game.getPlayers().stream().filter(p -> p.getNickname().equals(nick)).findFirst().orElseThrow();
        List<LeaderCard> leaders = leaderIndexes.stream().map(l -> player.getLeaders().get(l)).toList();
        player.chooseLeaders(leaders);
        checkEndSetup();
    }

    /**
     * Chooses the initial resources to take as a player.
     *
     * @param player  the player
     * @param reducedShelves the destination shelves
     * @throws IllegalActionException if the player cannot choose initial resources in the current state
     */
    public void chooseResources(String player, Map<Integer, Map<String, Integer>> reducedShelves) throws IllegalActionException, CannotChooseException, IllegalProductionActivationException {
        if (setupDone)
            throw new IllegalActionException();
        Map<Shelf, Map<ResourceType, Integer>> shelves = new HashMap<>();
        reducedShelves.forEach((key, value) -> shelves.put((Shelf) game.getShelfById(key).orElseThrow(), translateResources(value)));
        Player current = game.getPlayers().stream().filter(p -> p.getNickname().equals(player)).findFirst().orElseThrow();
        current.chooseResources(game, shelves);
        checkEndSetup();
    }

    /**
     * Swaps the content of two shelves of a player.
     *
     * @param nick the player
     * @param shelfId1     the first shelf
     * @param shelfId2     the second shelf
     * @throws IllegalActionException if the player cannot swap the contents of two shelves in the current state
     */
    public void swapShelves(String nick, Integer shelfId1, Integer shelfId2) throws IllegalActionException, IllegalResourceTransferException {
        if (!setupDone || game.hasEnded())
            throw new IllegalActionException();
        Player player = game.getPlayers().stream().filter(p -> p.getNickname().equals(nick)).findFirst().orElseThrow();
        checkCurrentPlayer(player);
        Shelf s1 = (Shelf) game.getShelfById(shelfId1).orElseThrow();
        Shelf s2 = (Shelf) game.getShelfById(shelfId2).orElseThrow();
        Shelf.swap(s1, s2);
    }

    /**
     * Makes a player activate a leader card.
     *
     * @param nick the player
     * @param leaderid the leader card to activate
     * @throws IllegalActionException if the player cannot activate a leader in the current state
     */
    public void activateLeader(String nick, Integer leaderid) throws IllegalActionException, CardRequirementsNotMetException {
        if (!setupDone || game.hasEnded())
            throw new IllegalActionException();
        Player player = game.getPlayers().stream().filter(p -> p.getNickname().equals(nick)).findFirst().orElseThrow();
        checkCurrentPlayer(player);
        LeaderCard leader = game.getLeaderById(leaderid).orElseThrow();
        leader.activate(player);
    }

    /**
     * Makes a player discard a leader card.
     *
     * @param nick the player
     * @param leaderid the leader card to discard
     * @throws IllegalActionException if the player cannot discard a leader in the current state
     */
    public void discardLeader(String nick, Integer leaderid) throws IllegalActionException, ActiveLeaderDiscardException {
        if (!setupDone || game.hasEnded())
            throw new IllegalActionException();
        Player player = game.getPlayers().stream().filter(p -> p.getNickname().equals(nick)).findFirst().orElseThrow();
        checkCurrentPlayer(player);
        LeaderCard leader = game.getLeaderById(leaderid).orElseThrow();
        player.discardLeader(game, leader);
    }

    /**
     * Takes resources from the market as a player.
     *
     * @param nick       the player
     * @param isRow        <code>true</code> if a row is selected; <code>false</code> if a column is selected.
     * @param index        index of the selected row or column
     * @param replacements a map of the chosen resources to take, if choices are applicable
     * @param reducedShelves      a map of the shelves where to add the taken resources, if possible
     * @throws IllegalActionException if the player cannot take resources from the market in the current state
     */
    public void takeMarketResources(String nick, boolean isRow, int index,
                                    Map<ResourceType, Integer> replacements,
                                    Map<Integer, Map<String, Integer>> reducedShelves) throws IllegalActionException, IllegalMarketTransferException {
        if (!setupDone || turnDone)
            throw new IllegalActionException();
        Player player = game.getPlayers().stream().filter(p -> p.getNickname().equals(nick)).findFirst().orElseThrow();
        checkCurrentPlayer(player);
        Map<Shelf, Map<ResourceType, Integer>> shelves = new HashMap<>();
        reducedShelves.forEach((key, value) -> shelves.put((Shelf) game.getShelfById(key).orElseThrow(), translateResources(value)));
        game.getMarket().takeResources(game, player, isRow, index, replacements, shelves);
        turnDone = true;
    }

    /**
     * Makes a player buy a development card from the development card grid.
     *
     * @param nick        the player
     * @param color         the color of the card to be bought
     * @param level         the level of the card to be bought
     * @param slotIndex     the index of the dev slot where to put the development card
     * @param reducedResContainers a map of the resource containers where to take the storable resources
     * @throws IllegalActionException if the player cannot buy a development card in the current state
     */
    public void buyDevCard(String nick, String color, int level, int slotIndex,
                           Map<Integer, Map<String, Integer>> reducedResContainers) throws IllegalActionException, CardRequirementsNotMetException, IllegalCardDepositException {
        if (!setupDone || turnDone)
            throw new IllegalActionException();
        Player player = game.getPlayers().stream().filter(p -> p.getNickname().equals(nick)).findFirst().orElseThrow();
        checkCurrentPlayer(player);
        Map<ResourceContainer, Map<ResourceType, Integer>> resContainers = new HashMap<>();
        reducedResContainers.forEach((key, value) -> resContainers.put(game.getShelfById(key).orElseThrow(), translateResources(value)));

        game.getDevCardGrid().buyDevCard(game, player, gameFactory.getDevCardColor(color).orElseThrow(), level, slotIndex, resContainers);
        turnDone = true;
    }

    /**
     * Makes a player activate a group of productions.
     *
     * @param nick          the player
     * @param reducedProdGroup the group of requested contemporary productions
     * @throws IllegalActionException if the player cannot activate a group of productions in the current state
     */
    public void activateProductionGroup(String nick, List<ReducedProductionRequest> reducedProdGroup) throws IllegalActionException, IllegalProductionActivationException {
        if (!setupDone || turnDone)
            throw new IllegalActionException();
        Player player = game.getPlayers().stream().filter(p -> p.getNickname().equals(nick)).findFirst().orElseThrow();
        checkCurrentPlayer(player);
        ProductionGroup productionGroup = new ProductionGroup(reducedProdGroup.stream().map (this::translateToProductionRequest).toList());
        productionGroup.activate(game, player);
        turnDone = true;
    }

    /**
     * Makes a player end his turn.
     *
     * @param nick the player
     * @throws IllegalActionException if the player cannot end the turn in the current state
     */
    public void endTurn(String nick) throws IllegalActionException, NoActivePlayersException {
        if (!setupDone || !turnDone)
            throw new IllegalActionException();
        Player player = game.getPlayers().stream().filter(p -> p.getNickname().equals(nick)).findFirst().orElseThrow();
        checkCurrentPlayer(player);
        game.onTurnEnd();
        turnDone = false;
    }

    /**
     * Returns the player who won the game, if the winner is a player.
     *
     * @return the player who won
     * @throws IllegalActionException if the player who won cannot be requested in the current state
     */
    public Optional<String> getWinnerPlayer() throws IllegalActionException {
        if (!game.hasEnded())
            throw new IllegalActionException();
        return game.getWinnerPlayer().map(Player::getNickname);
    }

    /**
     * Returns whether Lorenzo has won the game or not.
     *
     * @return <code>true</code> if Lorenzo is the winner of the game; <code>false</code> otherwise.
     * @throws IllegalActionException if this cannot be requested in the current state
     */
    public boolean isBlackWinner() throws IllegalActionException {
        if (!game.hasEnded())
            throw new IllegalActionException();
        return game.isBlackWinner();
    }

    /**
     * Check if the last necessary setup move has been made.
     */
    private void checkEndSetup() {
        if (game.getPlayers().stream().allMatch(p -> p.hasChosenLeaders() && p.hasChosenResources()))
            setupDone = true;
    }

    /**
     * Checks if the turn is of the given player.
     *
     * @param player the player to check
     * @throws IllegalActionException if the player can play in this turn
     */
    private void checkCurrentPlayer(Player player) throws IllegalActionException {
        if (!player.equals(game.getCurrentPlayer()))
            throw new IllegalActionException();
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
                inputContainers, outputContainers);
    }
}
