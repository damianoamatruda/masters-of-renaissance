package it.polimi.ingsw.common.events.mvevents;

import it.polimi.ingsw.common.View;

/** Leader card state update. */
public class UpdateLeader implements MVEvent {
    /** The ID of the card the action was called upon. */
    private final int leader;

    /** <code>true</code> if the card is active; <code>false</code> otherwise. */
    private final boolean active;

    /**
     * Class constructor.
     *
     * @param leader the ID of the card the action was called upon
     * @param active <code>true</code> if the card is active; <code>false</code> otherwise.
     */
    public UpdateLeader(int leader, boolean active) {
        this.leader = leader;
        this.active = active;
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
     * @return <code>true</code> if the card is active; <code>false</code> otherwise.
     */
    public boolean isActive() {
        return active;
    }
}
