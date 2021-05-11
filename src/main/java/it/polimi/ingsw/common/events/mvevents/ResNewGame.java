package it.polimi.ingsw.common.events.mvevents;

import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.events.mvevents.MVEvent;

/** Server response to a new game request. */
public class ResNewGame implements MVEvent {
    /** The number of players the new game is set to. */
    private final int count;
    /** The number of players that still need to join before the game can start. */
    private final int emptySeats;

    /**
     * Class constructor.
     * 
     * @param count the number of players the new game is set to
     * @param emptySeats the number of players that still need to join before the game can start
     */
    public ResNewGame(int count, int emptySeats) {
        this.count = count;
        this.emptySeats = emptySeats;
    }

    /**
     * @return the number of players that still need to join before the game can start
     */
    public int getEmptySeats() {
        return emptySeats;
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
