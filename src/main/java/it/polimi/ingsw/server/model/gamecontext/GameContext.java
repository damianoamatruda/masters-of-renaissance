package it.polimi.ingsw.server.model.gamecontext;

import it.polimi.ingsw.server.model.*;
import it.polimi.ingsw.server.model.cardrequirements.RequirementsNotMetException;
import it.polimi.ingsw.server.model.leadercards.IllegalActivationException;
import it.polimi.ingsw.server.model.leadercards.LeaderCard;
import it.polimi.ingsw.server.model.resourcecontainers.IllegalResourceTransferException;
import it.polimi.ingsw.server.model.resourcecontainers.ResourceContainer;
import it.polimi.ingsw.server.model.resourcecontainers.Shelf;
import it.polimi.ingsw.server.model.resourcetypes.ResourceType;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * This class manages the states and actions of a game.
 */
public class GameContext {
    /** The game. */
    final Game game;

    /** The current state of the game. */
    private GameState state;

    /**
     * Initializes a context for the given game.
     *
     * @param game the game
     */
    public GameContext(Game game) {
        this.game = game;
        this.state = new GameSetupState();
    }

    /**
     * Sets the state.
     *
     * @param state the next state
     */
    void setState(GameState state) {
        this.state = state;
    }

    /**
     * Returns the leader cards.
     *
     * @return the list of leader cards
     * @throws IllegalActionException if the list of leader cards cannot be requested in the current state
     */
    public List<LeaderCard> getLeaderCards() throws IllegalActionException {
        return state.getLeaderCards(this);
    }

    /**
     * Returns the development cards.
     *
     * @return the list of development cards
     * @throws IllegalActionException if the list of development cards cannot be requested in the current state
     */
    public List<DevelopmentCard> getDevelopmentCards() throws IllegalActionException {
        return state.getDevelopmentCards(this);
    }

    /**
     * Returns the game market.
     *
     * @return the market
     * @throws IllegalActionException if the game cannot be requested in the current state
     */
    public Market getMarket() throws IllegalActionException {
        return state.getMarket(this);
    }

    /**
     * Choose leaders from the hand of a player.
     *
     * @param player  the player
     * @param leaders the leader cards to choose
     * @throws IllegalActionException if the player cannot choose the leaders in the current state
     */
    public void chooseLeaders(Player player, List<LeaderCard> leaders) throws IllegalActionException, CannotChooseException {
        state.chooseLeaders(this, player, leaders);
    }

    /**
     * Chooses the initial resources to take as a player.
     *
     * @param player  the player
     * @param shelves the destination shelves
     * @throws IllegalActionException if the player cannot choose initial resources in the current state
     */
    public void chooseResources(Player player, Map<ResourceContainer, Map<ResourceType, Integer>> shelves) throws IllegalActionException, IllegalResourceTransferException, CannotChooseException, InvalidChoiceException {
        state.chooseResources(this, player, shelves);
    }

    /**
     * Swaps the content of two shelves of a player.
     *
     * @param player the player
     * @param s1     the first shelf
     * @param s2     the second shelf
     * @throws IllegalActionException if the player cannot swap the contents of two shelves in the current state
     */
    public void swapShelves(Player player, Shelf s1, Shelf s2) throws IllegalActionException, IllegalResourceTransferException {
        state.swapShelves(this, player, s1, s2);
    }

    /**
     * Takes resources from the market as a player.
     *
     * @param player       the player
     * @param isRow        <code>true</code> if a row is selected; <code>false</code> if a column is selected
     * @param index        index of the selected row or column
     * @param replacements a map of the chosen resources to take, if choices are applicable
     * @param shelves      a map of the shelves where to add the taken resources, if possible
     * @throws IllegalActionException if the player cannot take resources from the market in the current state
     */
    public void takeMarketResources(Player player, boolean isRow, int index, Map<ResourceType, Integer> replacements, Map<ResourceContainer, Map<ResourceType, Integer>> shelves) throws IllegalActionException, IllegalMarketTransferException {
        state.takeMarketResources(this, player, isRow, index, replacements, shelves);
    }

    /**
     * Makes a player buy a development card from the development card grid.
     *
     * @param player        the player
     * @param color         the color of the card to be bought
     * @param level         the level of the card to be bought
     * @param position      the position of the dev slot where to put the development card
     * @param resContainers a map of the resource containers where to take the storable resources
     * @throws IllegalActionException if the player cannot buy a development card in the current state
     */
    public void buyDevCard(Player player, DevCardColor color, int level, int position, Map<ResourceContainer, Map<ResourceType, Integer>> resContainers) throws IllegalActionException, RequirementsNotMetException, IllegalCardDepositException {
        state.buyDevCard(this, player, color, level, position, resContainers);
    }

    /**
     * Makes a player activate a group of productions.
     *
     * @param player          the player
     * @param productionGroup the group of requested contemporary productions
     * @throws IllegalActionException if the player cannot activate a group of productions in the current state
     */
    public void activateProductionGroup(Player player, ProductionGroup productionGroup) throws IllegalActionException, IllegalProductionActivationException {
        state.activateProductionGroup(this, player, productionGroup);
    }

    /**
     * Makes a player activate a leader card.
     *
     * @param player the player
     * @param leader the leader card to activate
     * @throws IllegalActionException if the player cannot activate a leader in the current state
     */
    public void activateLeader(Player player, LeaderCard leader) throws IllegalActionException, IllegalActivationException {
        state.activateLeader(this, player, leader);
    }

    /**
     * Makes a player discard a leader card.
     *
     * @param player the player
     * @param leader the leader card to discard
     * @throws IllegalActionException if the player cannot discard a leader in the current state
     */
    public void discardLeader(Player player, LeaderCard leader) throws IllegalActionException, IllegalActivationException, ActiveLeaderDiscardException {
        state.discardLeader(this, player, leader);
    }

    /**
     * Makes a player end his turn.
     *
     * @param player the player
     * @throws IllegalActionException if the player cannot end the turn in the current state
     */
    public void endTurn(Player player) throws NoActivePlayersException, IllegalActionException {
        state.endTurn(this, player);
    }

    /**
     * Returns the player who won the game, if the winner is a player.
     *
     * @return the player who won
     * @throws IllegalActionException if the player who won cannot be requested in the current state
     */
    public Optional<Player> getWinnerPlayer() throws IllegalActionException {
        return state.getWinnerPlayer(this);
    }

    /**
     * Returns whether Lorenzo has won the game or not.
     *
     * @return <code>true</code> if Lorenzo is the winner of the game; <code>false</code> otherwise.
     * @throws IllegalActionException if this cannot be requested in the current state
     */
    public boolean isBlackWinner() throws IllegalActionException {
        return state.isBlackWinner(this);
    }

    public Optional<Player> getPlayer(String nickname) {
        return game.getPlayers().stream().filter(p -> p.getNickname().equals(nickname)).findFirst();
    }
}
