package it.polimi.ingsw.common.events;

import it.polimi.ingsw.common.View;

public class UpdateFaithTrack implements MVEvent {
    private final boolean isBlackCross;
    private final int position;

    public UpdateFaithTrack(int newPos, boolean isBlackCross) {
        this.position = newPos;
        this.isBlackCross = isBlackCross;
    }

    public int getPosition() {
        return position;
    }

    public boolean isBlackCross() {
        return isBlackCross;
    }

    @Override
    public void handle(View view) {
        // TODO Auto-generated method stub
        
    }
}
