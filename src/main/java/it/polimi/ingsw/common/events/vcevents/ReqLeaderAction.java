package it.polimi.ingsw.common.events.vcevents;

/** Leader action request. */
public class ReqLeaderAction implements VCEvent {
    /** The ID of the leader card to be acted upon. */
    private final int leader;
    /**
     * True if request is to activate the leader, false if request is to discard the leader.
     */
    private final boolean isActivate;

    /**
     * @param leader     the leader card involved
     * @param isActivate true if action requested is an activation
     */
    public ReqLeaderAction(int leader, boolean isActivate) {
        this.leader = leader;
        this.isActivate = isActivate;
    }

    /**
     * @return the leader
     */
    public int getLeader() {
        return leader;
    }

    /**
     * @return the isActivate
     */
    public boolean isActivate() {
        return isActivate;
    }
}
