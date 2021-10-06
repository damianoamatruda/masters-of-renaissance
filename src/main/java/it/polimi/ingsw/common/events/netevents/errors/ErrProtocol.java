package it.polimi.ingsw.common.events.netevents.errors;

import it.polimi.ingsw.common.NetworkProtocolException;
import it.polimi.ingsw.common.events.netevents.NetEvent;

/**
 * Error generated upon unsuccessful parsing of a message.
 */
public class ErrProtocol implements NetEvent {
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
