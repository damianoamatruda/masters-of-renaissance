package it.polimi.ingsw.common.events.mvevents;

import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceContainer;

/** Resource container state update. */
public class UpdateResourceContainer implements MVEvent {
    /** The ID of the container. */
    private final ReducedResourceContainer container;

    /**
     * Class constructor.
     *
     * @param container the ID of the container
     */
    public UpdateResourceContainer(ReducedResourceContainer container) {
        this.container = container;
    }

    @Override
    public void handle(View view) {
        view.update(this);
    }

    /**
     * @return the new container state
     */
    public ReducedResourceContainer getContainer() {
        return container;
    }
}
