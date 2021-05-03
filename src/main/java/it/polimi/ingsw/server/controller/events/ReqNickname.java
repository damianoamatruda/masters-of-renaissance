package it.polimi.ingsw.server.controller.events;

import it.polimi.ingsw.server.view.View;

public class ReqNickname implements VCEvent {
    private String nickname;

    @Override
    public void handle(View view) {
        view.notify(this);
    }

    public String getNickname() {
        return nickname;
    }
}
