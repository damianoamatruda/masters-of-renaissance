package it.polimi.ingsw.server.controller.messages;

import it.polimi.ingsw.server.view.View;

public class ReqNickname implements Message {
    private String nickname;

    @Override
    public void handle(View view) {
        view.notify(this);
    }

    public String getNickname() {
        return nickname;
    }
}
