package it.polimi.ingsw.common.events;

import it.polimi.ingsw.common.View;

public class ResNewGame implements MVEvent {
    private final int count;

    /**
     * Class constructor.
     * 
     * @param count the number of players the new game is set to
     */
    public ResNewGame(int count) { this.count = count; }

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
