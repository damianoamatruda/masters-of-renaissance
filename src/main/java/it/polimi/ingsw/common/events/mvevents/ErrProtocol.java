package it.polimi.ingsw.common.events.mvevents;

import it.polimi.ingsw.common.NetworkProtocolException;

/** Error generated upon unsuccessful parsing of a message. */
public class ErrProtocol implements MVEvent {
    /** The reason why the message was unparsable */
    private final String msg;

    /**
     * Class constructor.
     *
     * @param e the protocol exception
     */
    public ErrProtocol(NetworkProtocolException e) {
        this.msg = e.getMessage();
    }

    /**
     * @return the reason why the message was unparsable
     */
    public String getMsg() {
        return msg;
    }
}
