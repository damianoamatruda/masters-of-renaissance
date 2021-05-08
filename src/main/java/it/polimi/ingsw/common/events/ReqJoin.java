package it.polimi.ingsw.common.events;

import it.polimi.ingsw.common.View;

public class ReqJoin implements VCEvent {
    private final String nickname;

    public ReqJoin(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public void handle(View view) {
        view.notify(this);
    }

    public String getNickname() {
        return nickname;
    }
}
