package it.polimi.ingsw.common.events.mvevents;

import it.polimi.ingsw.common.View;

/** Error message sent by the server that originates from errors in client requests. */
public class ErrAction implements MVEvent {
    /** Message containing the reason and details of why the client request failed. */
    private final String msg;

    /**
     * Class constructor.
     * 
     * @param msg the message containing the reason and details
     *            of why the client request failed
     */
    public ErrAction(String msg) {
        this.msg = msg;
    }

    @Override
    public void handle(View view) {
        view.update(this);
    }

    /**
     * @return the reason for which the client request failed
     */
    public String getMessage() {
        return msg;
    }
}
