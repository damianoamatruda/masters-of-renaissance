package it.polimi.ingsw.common.events.mvevents;

import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceContainer;

/** Resource container state update. */
public class UpdateResourceContainer extends ViewEvent {
    /** The ID of the container. */
    private final ReducedResourceContainer resContainer;

    /**
     * Class constructor.
     *
     * @param view
     * @param resContainer the ID of the container
     */
    public UpdateResourceContainer(View view, ReducedResourceContainer resContainer) {
        super(view);
        this.resContainer = resContainer;
    }

    public UpdateResourceContainer(ReducedResourceContainer resContainer) {
        this(null, resContainer);
    }

    /**
     * @return the new container state
     */
    public ReducedResourceContainer getResContainer() {
        return resContainer;
    }
}
