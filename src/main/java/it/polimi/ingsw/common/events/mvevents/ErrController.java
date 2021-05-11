package it.polimi.ingsw.common.events.mvevents;

import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.events.mvevents.MVEvent;

/** Error message signaling an uncaught exception. */
public class ErrController implements MVEvent {
    /** The message containing the error's reason. */
    private final String msg;

    /**
     * Class constructor.
     * 
     * @param e the uncaught exception
     */
    public ErrController(Exception e) {
        StringBuilder stringBuilder = new StringBuilder(e.getClass().getName());
        if (e.getMessage() != null)
            stringBuilder.append(": ").append(e.getMessage());
        this.msg = stringBuilder.toString();
    }

    @Override
    public void handle(View view) {
        view.update(this);
    }

    /**
     * @return the reason for the exception
     */
    public String getMessage() {
        return msg;
    }
}
