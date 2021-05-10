package it.polimi.ingsw.common.events;

import it.polimi.ingsw.common.View;

/** Error message sent by the server that originates from errors in client requests. */
public class ErrAction implements MVEvent {
    /** Message containing the reason and details of why the client request failed. */
    private final String message;

    /**
     * Class constructor.
     * 
     * @param message the message containing the reason and details
     *                of why the client request failed
     */
    public ErrAction(String message) {
        this.message = message;
    }

    @Override
    public void handle(View view) {
        view.update(this);
    }

    /**
     * @return the reason for which the client request failed
     */
    public String getMessage() {
        return message;
    }
}
