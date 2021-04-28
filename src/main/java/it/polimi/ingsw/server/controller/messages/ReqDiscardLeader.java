package it.polimi.ingsw.server.controller.messages;

import it.polimi.ingsw.server.model.ActiveLeaderDiscardException;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.gamecontext.GameContext;
import it.polimi.ingsw.server.model.gamecontext.IllegalActionException;

public class ReqDiscardLeader implements Message {
    private int leaderId;

    @Override
    public void handle(GameContext context, Player player) {
        System.out.println("Discarding leader of index " + leaderId + "...");
        try {
            context.discardLeader(player, player.getLeaders().get(leaderId));   //Might as well change signature to receive directly leaderId
        } catch (IllegalActionException | IndexOutOfBoundsException | ActiveLeaderDiscardException e) {    //To be handled!
            e.printStackTrace();
        }
    }
}
