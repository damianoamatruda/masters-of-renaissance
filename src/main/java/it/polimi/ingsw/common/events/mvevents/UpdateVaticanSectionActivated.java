package it.polimi.ingsw.common.events.mvevents;

import it.polimi.ingsw.common.View;

/** Vatican section activation state update. */
public class UpdateVaticanSectionActivated implements MVEvent {
    /** The section's ID. */
    private final int id;

    /**
     * Class constructor.
     *
     * @param id the section's ID
     */
    public UpdateVaticanSectionActivated(int id) {
        this.id = id;
    }

    @Override
    public void handle(View view) {
        view.update(this);
    }

    /**
     * @return the section's ID
     */
    public int getId() {
        return id;
    }
}
