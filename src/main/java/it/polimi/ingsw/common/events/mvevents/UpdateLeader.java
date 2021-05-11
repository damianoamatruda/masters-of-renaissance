package it.polimi.ingsw.common.events.mvevents;

import it.polimi.ingsw.common.View;

/** Leader card state update. */
public class UpdateLeader implements MVEvent {
    /** <code>true</code> if the card is now active; <code>false</code> otherwise. */
    private final boolean isActive;

    /** <code>true</code> if the card is now discard; <code>false</code> otherwise. */
    private final boolean isDiscarded;

    /** The ID of the card the action was called upon. */
    private final int leader;

    /**
     * Class constructor.
     *
     * @param leader      <code>true</code> if the card is now active; <code>false</code> otherwise.
     * @param isActive    <code>true</code> if the card is now discard; <code>false</code> otherwise.
     * @param isDiscarded the ID of the card the action was called upon
     */
    public UpdateLeader(int leader, boolean isActive, boolean isDiscarded) {
        this.leader = leader;
        this.isActive = isActive;
        this.isDiscarded = isDiscarded;
    }

    @Override
    public void handle(View view) {
        view.update(this);
    }

    /**
     * @return the leader card's ID
     */
    public int getLeader() {
        return leader;
    }

    /**
     * @return <code>true</code> if the card is now discard; <code>false</code> otherwise.
     */
    public boolean isDiscarded() {
        return isDiscarded;
    }

    /**
     * @return <code>true</code> if the card is now active; <code>false</code> otherwise.
     */
    public boolean isActive() {
        return isActive;
    }
}
