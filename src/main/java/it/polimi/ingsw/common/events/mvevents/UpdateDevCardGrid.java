package it.polimi.ingsw.common.events.mvevents;

import it.polimi.ingsw.common.reducedmodel.ReducedDevCardGrid;

/** Development card grid state update. */
public class UpdateDevCardGrid implements MVEvent {
    /** The new development card grid state. */
    private final ReducedDevCardGrid devCardGrid; // card ID == null means the stack was empty

    /**
     * Class constructor.
     *
     * @param devCardGrid the new development card grid state
     */
    public UpdateDevCardGrid(ReducedDevCardGrid devCardGrid) {
        this.devCardGrid = devCardGrid;
    }

    /**
     * @return the new development card grid state
     */
    public ReducedDevCardGrid getCards() {
        return devCardGrid;
    }
}
