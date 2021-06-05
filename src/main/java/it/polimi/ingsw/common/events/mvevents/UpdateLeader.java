package it.polimi.ingsw.common.events.mvevents;

/** Leader card state update. */
public class UpdateLeader implements MVEvent {
    /** The ID of the card the action was called upon. */
    private final int leader;

    /** <code>true</code> if the card is active; <code>false</code> otherwise. */
    private final boolean isActivated;

    /**
     * Class constructor.
     *
     * @param leader      the ID of the card the action was called upon
     * @param isActivated <code>true</code> if the card is active; <code>false</code> otherwise.
     */
    public UpdateLeader(int leader, boolean isActivated) {
        this.leader = leader;
        this.isActivated = isActivated;
    }

    /**
     * @return the leader card's ID
     */
    public int getLeader() {
        return leader;
    }

    /**
     * @return <code>true</code> if the card is active; <code>false</code> otherwise.
     */
    public boolean isActivated() {
        return isActivated;
    }
}
