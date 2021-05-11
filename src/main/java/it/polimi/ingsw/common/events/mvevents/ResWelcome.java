package it.polimi.ingsw.common.events.mvevents;

import it.polimi.ingsw.common.View;

/** Server response to a new socket connecting. */
public class ResWelcome implements MVEvent {
    @Override
    public void handle(View view) {
        view.update(this);
    }
}
