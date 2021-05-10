package it.polimi.ingsw.common.events;

import it.polimi.ingsw.common.View;

/** Server response to a new game request. */
public class ResNewGame implements MVEvent {
    /** The number of players the new game is set to. */
    private final int count; // TODO remove

    /**
     * Class constructor.
     * 
     * @param count the number of players the new game is set to
     */
    public ResNewGame(int count) {
        this.count = count;
    }

    /**
     * @return the new game's players' count
     */
    public int getCount() {
        return count;
    }

    @Override
    public void handle(View view) {
        // TODO Auto-generated method stub
        
    }
}
