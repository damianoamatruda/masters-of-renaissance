package it.polimi.ingsw.server.controller.messages;

import it.polimi.ingsw.server.model.leadercards.LeaderCard;
import it.polimi.ingsw.server.view.View;

import java.util.List;

public class ReqChooseLeaders implements Message {
    private List<LeaderCard> leaders;

    @Override
    public void handle(View view) {
        view.notify(this);
    }

    public List<LeaderCard> getLeaders() {
        return leaders;
    }
}
