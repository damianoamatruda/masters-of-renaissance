package it.polimi.ingsw.common.events;

import java.util.List;

import it.polimi.ingsw.common.View;

/** Development card grid state update. */
public class UpdateDevGrid implements MVEvent {
    // TODO reduced and docs
    /** The new list of topmost cards. */
    private final List<List<Integer>> topCards; // ID == 0 means the card was null -> stack was empty

    /**
     * Class constructor.
     * 
     * @param topCards the new list of topmost cards
     */
    public UpdateDevGrid(List<List<Integer>> topCards) {
        this.topCards = topCards;
    }

    /**
     * @return the new list of topmost cards
     */
    public List<List<Integer>> getCards() {
        return topCards;
    }

    @Override
    public void handle(View view) {
        // TODO Auto-generated method stub
        
    }
}
