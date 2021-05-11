package it.polimi.ingsw.common.events.mvevents;

import it.polimi.ingsw.common.View;

/** Update signaling the end of the setup phase. */
public class UpdateSetupDone implements MVEvent {
    @Override
    public void handle(View view) {
        view.update(this);
    }
}
