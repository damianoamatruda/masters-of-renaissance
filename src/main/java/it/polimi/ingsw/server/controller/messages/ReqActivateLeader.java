package it.polimi.ingsw.server.controller.messages;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.gamecontext.GameContext;
import it.polimi.ingsw.server.model.gamecontext.IllegalActionException;
import it.polimi.ingsw.server.model.leadercards.IllegalActivationException;

public class ReqActivateLeader implements Message {
    private int leaderId;

    @Override
    public void handle(GameContext context, Player player) {
        try {
            context.activateLeader(player, player.getLeaders().get(leaderId));
        } catch (IllegalActionException | IllegalActivationException e) {
            e.printStackTrace();
        }
    }
}
