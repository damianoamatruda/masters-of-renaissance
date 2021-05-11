package it.polimi.ingsw.common.events.vcevents;

import it.polimi.ingsw.common.View;

/** Client request for leader activation. */
public class ReqActivateLeader implements VCEvent {
    /** The ID of the leader card to be activated */
    private final int leader;

    /**
     * Class constructor.
     * 
     * @param leader the ID of the leader card to be activated
     */
    public ReqActivateLeader(int leader) {
        this.leader = leader;
    }

    @Override
    public void handle(View view) {
        view.notify(this);
    }

    /**
     * @return the ID of the leader card to be activated
     */
    public int getLeader() {
        return leader;
    }
}
