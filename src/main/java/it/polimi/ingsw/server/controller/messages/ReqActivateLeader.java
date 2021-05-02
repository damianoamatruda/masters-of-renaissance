package it.polimi.ingsw.server.controller.messages;

import it.polimi.ingsw.server.model.leadercards.LeaderCard;
import it.polimi.ingsw.server.view.View;

public class ReqActivateLeader implements Message {
    private LeaderCard leader;

    @Override
    public void handle(View view) {
        view.notify(this);
    }

    public LeaderCard getLeader() {
        return leader;
    }
}
