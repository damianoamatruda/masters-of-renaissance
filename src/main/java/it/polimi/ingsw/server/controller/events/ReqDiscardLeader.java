package it.polimi.ingsw.server.controller.events;

import it.polimi.ingsw.server.model.leadercards.LeaderCard;
import it.polimi.ingsw.server.view.View;

public class ReqDiscardLeader implements VCEvent {
    private LeaderCard leader;

    @Override
    public void handle(View view) {
        view.notify(this);
    }

    public LeaderCard getLeader() {
        return leader;
    }
}
