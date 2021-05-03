package it.polimi.ingsw.server.controller.events;

import it.polimi.ingsw.server.model.leadercards.LeaderCard;
import it.polimi.ingsw.server.view.View;

import java.util.List;

public class ReqChooseLeaders implements VCEvent {
    private List<LeaderCard> leaders;

    @Override
    public void handle(View view) {
        view.notify(this);
    }

    public List<LeaderCard> getLeaders() {
        return leaders;
    }
}
