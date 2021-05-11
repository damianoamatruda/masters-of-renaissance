package it.polimi.ingsw.common.events.vcevents;

import it.polimi.ingsw.common.View;

/** Client request to completely disconnect from the server. */
public class GoodBye implements VCEvent {
    @Override
    public void handle(View view) {
        view.notify(this);
    }
}
