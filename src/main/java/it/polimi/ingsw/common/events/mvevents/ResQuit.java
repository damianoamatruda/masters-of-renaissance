package it.polimi.ingsw.common.events.mvevents;

import it.polimi.ingsw.common.View;

/**
 * Server confirmation of successful client disconnection routine.
 */
public class ResQuit extends ViewEvent {
    /**
     * @param view the view to address the message to
     */
    public ResQuit(View view) {
        super(view);
    }
}
