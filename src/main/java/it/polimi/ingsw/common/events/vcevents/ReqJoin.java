package it.polimi.ingsw.common.events.vcevents;

import it.polimi.ingsw.common.View;

/** Client request to connect to the server's lobby. */
public class ReqJoin implements VCEvent {
    /** The nickname to connect with. */
    private final String nickname;

    /**
     * Class constructor.
     * 
     * @param nickname the chosen nickname to connect to the lobby with
     */
    public ReqJoin(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public void handle(View view) {
        view.emit(this);
    }

    /**
     * @return the nickname to connect with
     */
    public String getNickname() {
        return nickname;
    }
}
