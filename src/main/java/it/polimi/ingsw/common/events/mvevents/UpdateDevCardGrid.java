package it.polimi.ingsw.common.events.mvevents;

import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.reducedmodel.ReducedDevCardGrid;

/** Development card grid state update. */
public class UpdateDevCardGrid extends ViewEvent {
    /** The new development card grid state. */
    private final ReducedDevCardGrid devCardGrid; // card ID == null means the stack was empty

    /**
     * Class constructor.
     *
     * @param view
     * @param devCardGrid the new development card grid state
     */
    public UpdateDevCardGrid(View view, ReducedDevCardGrid devCardGrid) {
        super(view);
        this.devCardGrid = devCardGrid;
    }

    /**
     * @return the new development card grid state
     */
    public ReducedDevCardGrid getCards() {
        return devCardGrid;
    }
}
