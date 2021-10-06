package it.polimi.ingsw.common.events.mvevents;

import it.polimi.ingsw.common.reducedmodel.ReducedResourceContainer;

/**
 * Resource container state update.
 */
public class UpdateResourceContainer implements MVEvent {
    /** The ID of the container. */
    private final ReducedResourceContainer resContainer;

    /**
     * Class constructor.
     *
     * @param resContainer the ID of the container
     */
    public UpdateResourceContainer(ReducedResourceContainer resContainer) {
        this.resContainer = resContainer;
    }

    /**
     * @return the new container state
     */
    public ReducedResourceContainer getResContainer() {
        return resContainer;
    }
}
