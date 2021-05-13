package it.polimi.ingsw.common.events.vcevents;

import it.polimi.ingsw.common.View;

public class ReqHeartbeat implements VCEvent {
    @Override
    public void handle(View view) {
        view.update(new ResHeartbeat());
    }
}
