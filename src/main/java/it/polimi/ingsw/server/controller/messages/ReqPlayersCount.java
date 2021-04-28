package it.polimi.ingsw.server.controller.messages;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.gamecontext.GameContext;

public class ReqPlayersCount implements Message {
    private int count;

    @Override
    public void handle(GameContext context, Player player) {

    }
}
