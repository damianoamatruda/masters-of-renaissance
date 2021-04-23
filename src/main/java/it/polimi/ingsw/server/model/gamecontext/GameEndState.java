package it.polimi.ingsw.server.model.gamecontext;

import it.polimi.ingsw.server.model.Player;

import java.util.Optional;

/**
 * This class represent the state of an ended game.
 */
public class GameEndState extends GameState {
    @Override
    public Optional<Player> getWinnerPlayer(GameContext context) {
        return context.game.getWinnerPlayer();
    }

    @Override
    public boolean isBlackWinner(GameContext context) {
        return context.game.isBlackWinner();
    }
}
