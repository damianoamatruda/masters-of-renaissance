package it.polimi.ingsw.common.events;

import it.polimi.ingsw.common.View;

/** Client request to end the player's turn. */
public class ReqTurnEnd implements VCEvent {
    @Override
    public void handle(View view) {
        view.notify(this);
    }
}
