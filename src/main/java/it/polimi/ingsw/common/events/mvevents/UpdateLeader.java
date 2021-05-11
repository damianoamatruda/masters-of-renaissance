package it.polimi.ingsw.common.events.mvevents;

import it.polimi.ingsw.common.View;

/** Leader card state update. */
public class UpdateLeader implements MVEvent {
    /** Whether the card is now active. */
    private final boolean isActive;
    /** Whether the card is now discarded. */
    private final boolean isDiscarded;
    /** The ID of the card the action was called upon. */
    private final int leader;

    /**
     * Class constructor.
     * 
     * @param leader      whether the card is now active
     * @param isActive    whether the card is now discarded
     * @param isDiscarded the ID of the card the action was called upon
     */
    public UpdateLeader(int leader, boolean isActive, boolean isDiscarded) {
        this.leader = leader;
        this.isActive = isActive;
        this.isDiscarded = isDiscarded;
    }

    /**
     * @return the leader card's ID
     */
    public int getLeader() {
        return leader;
    }

    /**
     * @return whether the leader was discarded
     */
    public boolean isDiscarded() {
        return isDiscarded;
    }

    /**
     * @return whether the leader was activated
     */
    public boolean isActive() {
        return isActive;
    }

    @Override
    public void handle(View view) {
        // TODO Auto-generated method stub
        
    }
}
