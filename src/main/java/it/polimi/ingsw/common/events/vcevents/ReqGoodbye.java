package it.polimi.ingsw.common.events.vcevents;

import it.polimi.ingsw.common.View;

/** Client request to completely disconnect from the server. */
public class ReqGoodbye implements VCEvent {
    @Override
    public void handle(View view) {
        view.emit(this);
    }
}
