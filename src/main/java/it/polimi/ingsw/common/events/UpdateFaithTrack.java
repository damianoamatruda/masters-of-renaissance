package it.polimi.ingsw.common.events;

import it.polimi.ingsw.common.View;

/** Faith track state update. */
public class UpdateFaithTrack implements MVEvent {
    // TODO nickname and docs
    /** Whether the new position refers to the black cross marker. */
    private final boolean isBlackCross;
    /** The new marker position. */
    private final int position;

    /**
     * Class constructor.
     * 
     * @param newPos       whether the new position refers to the black cross marker
     * @param isBlackCross the new marker position
     */
    public UpdateFaithTrack(int newPos, boolean isBlackCross) {
        this.position = newPos;
        this.isBlackCross = isBlackCross;
    }

    /**
     * @return the new marker position
     */
    public int getPosition() {
        return position;
    }

    /**
     * @return whether the new position refers to the black cross marker
     */
    public boolean isBlackCross() {
        return isBlackCross;
    }

    @Override
    public void handle(View view) {
        // TODO Auto-generated method stub
        
    }
}
