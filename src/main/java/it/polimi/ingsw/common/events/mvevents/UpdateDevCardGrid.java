package it.polimi.ingsw.common.events.mvevents;

import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.events.mvevents.MVEvent;
import it.polimi.ingsw.common.reducedmodel.ReducedDevCardGrid;

/** Development card grid state update. */
public class UpdateDevCardGrid implements MVEvent {
    /** The new development card grid state. */
    private final ReducedDevCardGrid topCards; // ID == 0 means the card was null -> stack was empty

    /**
     * Class constructor.
     * 
     * @param topCards the new development card grid state
     */
    public UpdateDevCardGrid(ReducedDevCardGrid topCards) {
        this.topCards = topCards;
    }

    /**
     * @return the new development card grid state
     */
    public ReducedDevCardGrid getCards() {
        return topCards;
    }

    @Override
    public void handle(View view) {
        // TODO Auto-generated method stub
        
    }
}
