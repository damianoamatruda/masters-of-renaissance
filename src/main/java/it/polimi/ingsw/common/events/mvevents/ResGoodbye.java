package it.polimi.ingsw.common.events.mvevents;

import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.events.mvevents.MVEvent;

/** Server answer to a disconnection request. */
public class ResGoodbye implements MVEvent {
    @Override
    public void handle(View view) {
        view.update(this);
    }
}
