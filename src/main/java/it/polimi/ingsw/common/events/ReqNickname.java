package it.polimi.ingsw.common.events;

import it.polimi.ingsw.common.ControllerObservable;

public class ReqNickname implements VCEvent {
    private final String nickname;

    public ReqNickname(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public void handle(ControllerObservable view) {
        view.notify(this);
    }

    public String getNickname() {
        return nickname;
    }
}
