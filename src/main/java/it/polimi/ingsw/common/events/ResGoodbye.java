package it.polimi.ingsw.common.events;

import it.polimi.ingsw.common.View;

/** Server answer to a disconnection request. */
public class ResGoodbye implements MVEvent {
    // TODO comms docs
    @Override
    public void handle(View view) {
        view.update(this);
    }
}
