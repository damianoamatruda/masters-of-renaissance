package it.polimi.ingsw.common.events;

import it.polimi.ingsw.common.View;

/** Server response to a new socket connecting. */
public class ResWelcome implements MVEvent {
    // TODO not in docs
    @Override
    public void handle(View view) {
        view.update(this);
    }
}
