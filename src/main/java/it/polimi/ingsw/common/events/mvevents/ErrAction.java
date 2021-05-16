package it.polimi.ingsw.common.events.mvevents;

/** Error message sent by the server that originates from errors in client requests. */
public class ErrAction implements MVEvent {
    /** Message containing the reason and details of why the client request failed. */
    private final String msg;

    /**
     * Class constructor.
     *
     * @param msg the message containing the reason and details of why the client request failed
     */
    public ErrAction(String msg) {
        this.msg = msg;
    }

    /**
     * Class constructor.
     *
     * @param e the action exception
     */
    public ErrAction(Exception e) {
        StringBuilder stringBuilder = new StringBuilder(e.getClass().getName());
        if (e.getMessage() != null)
            stringBuilder.append(": ").append(e.getMessage());
        this.msg = stringBuilder.toString();
    }

    /**
     * @return the reason for which the client request failed
     */
    public String getMessage() {
        return msg;
    }
}
