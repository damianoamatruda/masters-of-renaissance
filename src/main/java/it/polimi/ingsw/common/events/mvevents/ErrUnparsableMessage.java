package it.polimi.ingsw.common.events.mvevents;

import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.events.mvevents.MVEvent;

/** Error generated upon unsuccessful parsing of a message. */
public class ErrUnparsableMessage implements MVEvent {
    /** The reason why the message was unparsable */
    private final String message;

    /**
     * Class constructor.
     * 
     * @param message the reason why the message was unparsable
     */
    public ErrUnparsableMessage(String message) {
        this.message = message;
    }

    @Override
    public void handle(View view) {
        view.update(this);
    }

    /**
     * @return the reason why the message was unparsable
     */
    public String getMessage() {
        return message;
    }
}
