package it.polimi.ingsw.server.controller.messages;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.gamecontext.GameContext;

public class ReqNickname implements Message {
    private String nickname;

    @Override
    public void handle(GameContext context, Player player) {

    }
}
