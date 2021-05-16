package it.polimi.ingsw.common.events.mvevents;

/** Error generated upon unsuccessful parsing of a message. */
public class ErrProtocol implements MVEvent {
    /** The reason why the message was unparsable */
    private final String msg;

    /**
     * Class constructor.
     *
     * @param msg the reason why the message was unparsable
     */
    public ErrProtocol(String msg) {
        this.msg = msg;
    }

    /**
     * @return the reason why the message was unparsable
     */
    public String getMsg() {
        return msg;
    }
}
