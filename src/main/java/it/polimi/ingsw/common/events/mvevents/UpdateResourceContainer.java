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
     * @param id      the ID of the container
     * @param content the new state of the container
     */
    public UpdateResourceContainer(ReducedResourceContainer container) {
        this.container = container;
    }

    /**
     * @return the new container state
     */
    public ReducedResourceContainer getContainer() {
        return container;
    }

    @Override
    public void handle(View view) {
        // TODO Auto-generated method stub
        
    }
}
