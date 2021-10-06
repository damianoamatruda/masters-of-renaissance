package it.polimi.ingsw.common.events.vcevents;

/**
 * Leader action request.
 */
public class ReqLeaderAction implements VCEvent {
    /** The ID of the leader card to be acted upon. */
    private final int leader;

    /** <code>true</code> if request is to activate the leader; <code>false</code> if request is to discard the
     * leader. */
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
     * @return whether it is requested to activate or to discard the leader
     */
    public boolean isActivate() {
        return isActivate;
    }
}
