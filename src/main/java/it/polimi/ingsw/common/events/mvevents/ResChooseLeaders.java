package it.polimi.ingsw.common.events.mvevents;

import it.polimi.ingsw.common.View;

import java.util.List;

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

    @Override
    public void handle(View view) {
        view.update(this);
    }

    /**
     * @return the IDs of the chosen leader cards
     */
    public List<Integer> getLeadersId() {
        return leadersId;
    }
}
