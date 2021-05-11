package it.polimi.ingsw.common.events.vcevents;

import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.events.vcevents.VCEvent;

/** Client request to end the player's turn. */
public class ReqTurnEnd implements VCEvent {
    @Override
    public void handle(View view) {
        view.notify(this);
    }
}
