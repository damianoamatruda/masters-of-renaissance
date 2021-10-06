package it.polimi.ingsw.common.events.vcevents;

/**
 * Client request to connect to the server's lobby.
 */
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

    /**
     * @return the nickname to connect with
     */
    public String getNickname() {
        return nickname;
    }
}
