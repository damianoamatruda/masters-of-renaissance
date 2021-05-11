package it.polimi.ingsw.common.events.vcevents;

import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.events.vcevents.VCEvent;

/** Client request for a new game. */
public class ReqNewGame implements VCEvent {
    /** The number of players needed to start the game. */
    private final int count;

    /**
     * Class constructor.
     * 
     * @param count the number of players needed to start the game
     */
    public ReqNewGame(int count) {
        this.count = count;
    }

    @Override
    public void handle(View view) {
        view.notify(this);
    }

    /**
     * @return the number of players needed to start the game
     */
    public int getCount() {
        return count;
    }
}
