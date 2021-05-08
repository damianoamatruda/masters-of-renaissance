package it.polimi.ingsw.common.events;

import java.util.ArrayList;
import java.util.List;

import it.polimi.ingsw.common.View;
import it.polimi.ingsw.server.model.leadercards.LeaderCard;

public class ResChooseLeaders implements MVEvent {
    private final List<Integer> leadersId;

    public ResChooseLeaders(List<Integer> leadersId) {
        this.leadersId = leadersId;
    }

    public static List<Integer> extractLeadersIDs(List<LeaderCard> leaders) {
        List<Integer> ids = new ArrayList<>();

        for (LeaderCard l : leaders)
            ids.add(l.getId());
        
        return ids;
    }

    public List<Integer> getLeadersId() {
        return leadersId;
    }

    @Override
    public void handle(View view) {
        // TODO Auto-generated method stub
        
    }
}
