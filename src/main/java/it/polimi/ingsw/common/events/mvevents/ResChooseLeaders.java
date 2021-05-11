package it.polimi.ingsw.common.events.mvevents;

import java.util.List;

import it.polimi.ingsw.common.View;

/** Server confirmation to the leader choice request during player setup. */
public class ResChooseLeaders implements MVEvent {
    /** The IDs of the chosen leader cards. */
    private final List<Integer> leadersId;

    /**
     * Class constructor.
     * 
     * @param leadersId the IDs of the chosen leader cards
     */
    public ResChooseLeaders(List<Integer> leadersId) {
        this.leadersId = leadersId;
    }

    /**
     * @return the IDs of the chosen leader cards
     */
    public List<Integer> getLeadersId() {
        return leadersId;
    }

    @Override
    public void handle(View view) {
        // TODO Auto-generated method stub
        
    }
}
