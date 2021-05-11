package it.polimi.ingsw.common.events.mvevents;

import it.polimi.ingsw.common.View;

/** Vatican section activation state update. */
public class UpdateActivatedVatSection implements MVEvent {
    /** The section's ID. */
    private final int id;

    /**
     * Class constructor.
     * 
     * @param id the section's ID
     */
    public UpdateActivatedVatSection(int id) {
        this.id = id;
    }

    /**
     * @return the section's ID
     */
    public int getId() {
        return id;
    }

    @Override
    public void handle(View view) {
        // TODO Auto-generated method stub
        
    }
}
