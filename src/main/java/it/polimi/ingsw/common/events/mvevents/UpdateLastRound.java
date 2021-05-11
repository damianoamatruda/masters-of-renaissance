package it.polimi.ingsw.common.events.mvevents;

import it.polimi.ingsw.common.View;

/** State update that signals the start of the last round of the match. */
public class UpdateLastRound implements MVEvent {
    @Override
    public void handle(View view) {
        view.update(this);
    }
}
