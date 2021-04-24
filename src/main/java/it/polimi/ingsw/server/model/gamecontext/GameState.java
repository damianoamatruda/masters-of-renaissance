package it.polimi.ingsw.server.model.gamecontext;

import it.polimi.ingsw.server.model.*;
import it.polimi.ingsw.server.model.cardrequirements.RequirementsNotMetException;
import it.polimi.ingsw.server.model.leadercards.LeaderCard;
import it.polimi.ingsw.server.model.resourcecontainers.IllegalResourceTransferException;
import it.polimi.ingsw.server.model.resourcecontainers.ResourceContainer;
import it.polimi.ingsw.server.model.resourcecontainers.Shelf;
import it.polimi.ingsw.server.model.resourcecontainers.Warehouse;
import it.polimi.ingsw.server.model.resourcetypes.ResourceType;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * This class represents a game state.
 */
abstract public class GameState {
    /**
     * Returns the game market.
     *
     * @param context the context
     * @return the market
     * @throws IllegalActionException if the game cannot be requested in the current state
     */
    public Market getMarket(GameContext context) throws IllegalActionException {
        throw new IllegalActionException();
    }

    /**
     * Choose leaders from the hand of a player.
     *
     * @param context the context
     * @param player  the player
     * @param leaders the leader cards to choose
     * @throws IllegalActionException if the player cannot choose the leaders in the current state
     */
    public void chooseLeaders(GameContext context, Player player, List<LeaderCard> leaders) throws IllegalActionException, CannotChooseException {
        throw new IllegalActionException();
    }

    /**
     * Chooses an initial resource to take as a player.
     *
     * @param context  the context
     * @param player   the player
     * @param resource the chosen resource
     * @param shelf    the destination warehouse shelf
     * @throws IllegalActionException if the player cannot choose an initial resource in the current state
     */
    public void chooseResource(GameContext context, Player player, ResourceType resource, Warehouse.WarehouseShelf shelf) throws IllegalActionException, IllegalResourceTransferException, CannotChooseException, InvalidChoiceException {
        throw new IllegalActionException();
    }

    /**
     * Swaps the content of two shelves of a player.
     *
     * @param context the context
     * @param player  the player
     * @param s1      the first shelf
     * @param s2      the second shelf
     * @throws IllegalActionException if the player cannot swap the contents of two shelves in the current state
     */
    public void swapShelves(GameContext context, Player player, Shelf s1, Shelf s2) throws IllegalActionException, IllegalResourceTransferException {
        throw new IllegalActionException();
    }

    /**
     * Takes resources from the market as a player.
     *
     * @param context      the context
     * @param player       the player
     * @param isRow        <code>true</code> if a row is selected; <code>false</code> if a column is selected
     * @param index        index of the selected row or column
     * @param replacements a map of the chosen resources to take, if choices are applicable
     * @param shelves      a map of the shelves where to add the taken resources, if possible
     * @throws IllegalActionException if the player cannot take resources from the market in the current state
     */
    public void takeMarketResources(GameContext context, Player player, boolean isRow, int index, Map<ResourceType, Integer> replacements, Map<ResourceContainer, Map<ResourceType, Integer>> shelves) throws IllegalActionException, IllegalMarketTransferException {
        throw new IllegalActionException();
    }

    /**
     * Makes a player buy a development card from the development card grid.
     *
     * @param context       the context
     * @param player        the player
     * @param color         the color of the card to be bought
     * @param level         the level of the card to be bought
     * @param position      the position of the dev slot where to put the development card
     * @param resContainers a map of the resource containers where to take the storable resources
     * @throws IllegalActionException if the player cannot buy a development card in the current state
     */
    public void buyDevCard(GameContext context, Player player, DevCardColor color, int level, int position, Map<ResourceContainer, Map<ResourceType, Integer>> resContainers) throws IllegalActionException, RequirementsNotMetException, IllegalCardDepositException {
        throw new IllegalActionException();
    }

    /**
     * Makes a player activate a group of productions.
     *
     * @param context         the context
     * @param player          the player
     * @param productionGroup the group of requested contemporary productions
     * @throws IllegalActionException if the player cannot activate a group of productions in the current state
     */
    public void activateProductionGroup(GameContext context, Player player, ProductionGroup productionGroup) throws IllegalActionException, IllegalProductionActivationException {
        throw new IllegalActionException();
    }

    /**
     * Makes a player discard a leader.
     *
     * @param context the context
     * @param player  the player
     * @param index   the index of the card to be discarded
     * @throws IllegalActionException if the player cannot discard a leader in the current state
     */
    public void discardLeader(GameContext context, Player player, int index) throws IllegalActionException, AlreadyActiveException {
        throw new IllegalActionException();
    }

    /**
     * Action performed when a player ends the turn. This method appends the current player as last of the list, and
     * gets the next (active) player from the head.
     * <p>
     * If next player is inactive, the operation is repeated until an active player is found.
     *
     * @param context the context
     * @param player  the player
     * @throws AllInactiveException   all players are set to inactive
     * @throws IllegalActionException if the player cannot end the turn in the current state
     */
    public void endTurn(GameContext context, Player player) throws IllegalActionException, AllInactiveException {
        throw new IllegalActionException();
    }

    /**
     * Returns the player who won the game, if the winner is a player.
     *
     * @param context the context
     * @return the optional player
     * @throws IllegalActionException if the player who won cannot be requested in the current state
     */
    public Optional<Player> getWinnerPlayer(GameContext context) throws IllegalActionException {
        throw new IllegalActionException();
    }

    /**
     * Returns whether Lorenzo has won the game or not.
     *
     * @param context the context
     * @return <code>true</code> if Lorenzo is the winner of the game; <code>false</code> otherwise.
     * @throws IllegalActionException if this cannot be requested in the current state
     */
    public boolean isBlackWinner(GameContext context) throws IllegalActionException {
        throw new IllegalActionException();
    }
}
