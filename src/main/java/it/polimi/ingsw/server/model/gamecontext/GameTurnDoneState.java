package it.polimi.ingsw.server.model.gamecontext;

import it.polimi.ingsw.server.model.NoActivePlayersException;
import it.polimi.ingsw.server.model.Player;

/**
 * This class represents the state of a game during the turn of a player that has already made the mandatory move.
 */
public class GameTurnDoneState extends GameTurnState {
    @Override
    public void endTurn(GameContext context, Player player) throws IllegalActionException, NoActivePlayersException {
        checkCurrentPlayer(context, player);
        context.game.onTurnEnd();
        context.setState(context.game.hasEnded() ? new GameEndState() : new GameTurnNotDoneState());
    }
}
