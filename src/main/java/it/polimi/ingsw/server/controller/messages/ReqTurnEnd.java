package it.polimi.ingsw.server.controller.messages;

import it.polimi.ingsw.server.model.NoActivePlayersException;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.gamecontext.GameContext;
import it.polimi.ingsw.server.model.gamecontext.IllegalActionException;

public class ReqTurnEnd implements Message {
    @Override
    public void handle(GameContext context, Player player) {
        try {
            context.endTurn(player);
        } catch (NoActivePlayersException | IllegalActionException e) {
            e.printStackTrace();
        }
    }
}
