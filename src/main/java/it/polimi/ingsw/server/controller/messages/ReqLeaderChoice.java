package it.polimi.ingsw.server.controller.messages;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.gamecontext.GameContext;

import java.util.List;

public class ReqLeaderChoice implements Message {
    private List<Integer> leadersId;

    @Override
    public void handle(GameContext context, Player player) {

    }
}
