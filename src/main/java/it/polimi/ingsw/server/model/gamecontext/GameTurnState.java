package it.polimi.ingsw.server.model.gamecontext;

import it.polimi.ingsw.server.model.AlreadyActiveException;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.leadercards.IllegalActivationException;
import it.polimi.ingsw.server.model.leadercards.LeaderCard;
import it.polimi.ingsw.server.model.resourcecontainers.IllegalResourceTransferException;
import it.polimi.ingsw.server.model.resourcecontainers.Shelf;

/**
 * This class represents a general turn game state.
 */
abstract public class GameTurnState extends GameState {
    @Override
    public void swapShelves(GameContext context, Player player, Shelf s1, Shelf s2) throws IllegalActionException, IllegalResourceTransferException {
        checkCurrentPlayer(context, player);
        Shelf.swap(s1, s2);
    }

    @Override
    public void activateLeader(GameContext context, Player player, LeaderCard leader) throws IllegalActionException, IllegalActivationException {
        checkCurrentPlayer(context, player);
        leader.activate(player);
    }

    @Override
    public void discardLeader(GameContext context, Player player, LeaderCard leader) throws IllegalActionException, IllegalActivationException, AlreadyActiveException {
        checkCurrentPlayer(context, player);
        player.discardLeader(context.game, leader);
    }

    /**
     * Checks if the turn is of the given player.
     *
     * @param context the context
     * @param player  the player to check
     * @throws IllegalActionException if the player can play in this turn
     */
    protected void checkCurrentPlayer(GameContext context, Player player) throws IllegalActionException {
        if (!player.equals(context.game.getCurrentPlayer()))
            throw new IllegalActionException();
    }
}
