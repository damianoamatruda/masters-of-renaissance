package it.polimi.ingsw.common.events.netevents.errors;

import it.polimi.ingsw.common.events.netevents.NetEvent;

/** Error message signaling an uncaught exception. */
public class ErrRuntime implements NetEvent {
    /** The message containing the error's reason. */
    private final String msg;

    /**
     * Class constructor.
     *
     * @param e the uncaught exception
     */
    public ErrRuntime(Exception e) {
        StringBuilder stringBuilder = new StringBuilder(e.getClass().getName());
        if (e.getMessage() != null)
            stringBuilder.append(": ").append(e.getMessage());
        this.msg = stringBuilder.toString();
    }

    /**
     * @return the reason for the exception
     */
    public String getMessage() {
        return msg;
    }
}
