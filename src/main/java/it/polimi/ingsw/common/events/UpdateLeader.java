package it.polimi.ingsw.common.events;

import it.polimi.ingsw.common.View;

public class UpdateLeader implements MVEvent{
    private final boolean isActive, isDiscarded;
    private final int leader;

    public UpdateLeader(int leader, boolean isActive, boolean isDiscarded) {
        this.leader = leader;
        this.isActive = isActive;
        this.isDiscarded = isDiscarded;
    }

    /**
     * @return the leader
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
