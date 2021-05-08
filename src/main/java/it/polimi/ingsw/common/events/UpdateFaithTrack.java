package it.polimi.ingsw.common.events;

import it.polimi.ingsw.common.View;

public class UpdateFaithTrack implements MVEvent {
    private final boolean isLiM; // TODO check LiM update
    private final int position;

    public UpdateFaithTrack(int newPos, boolean isLim) {
        this.position = newPos;
        this.isLiM = isLim;
    }

    public int getPosition() {
        return position;
    }

    public boolean isLiM() {
        return isLiM;
    }

    @Override
    public void handle(View view) {
        // TODO Auto-generated method stub
        
    }
}
