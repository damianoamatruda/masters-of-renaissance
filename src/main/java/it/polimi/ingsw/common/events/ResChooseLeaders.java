package it.polimi.ingsw.common.events;

import java.util.List;

import it.polimi.ingsw.common.View;

public class ResChooseLeaders implements MVEvent {
    private final List<Integer> leadersId;

    public ResChooseLeaders(List<Integer> leadersId) {
        this.leadersId = leadersId;
    }

    public List<Integer> getLeadersId() {
        return leadersId;
    }

    @Override
    public void handle(View view) {
        // TODO Auto-generated method stub
        
    }
}
