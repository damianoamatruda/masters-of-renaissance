package it.polimi.ingsw.common.events.mvevents;

/**
 * Leader card state update.
 */
public class UpdateActivateLeader implements MVEvent {
    /** The ID of the card the action was called upon. */
    private final int leader;

    /**
     * Class constructor.
     *
     * @param leader the ID of the card the action was called upon
     */
    public UpdateActivateLeader(int leader) {
        this.leader = leader;
    }

    /**
     * @return the leader card's ID
     */
    public int getLeader() {
        return leader;
    }
}
