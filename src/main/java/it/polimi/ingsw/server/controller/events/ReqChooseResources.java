package it.polimi.ingsw.server.controller.events;

import it.polimi.ingsw.server.model.resourcecontainers.ResourceContainer;
import it.polimi.ingsw.server.model.resourcetypes.ResourceType;
import it.polimi.ingsw.server.view.View;

import java.util.Map;

public class ReqChooseResources implements VCEvent {
    private Map<ResourceContainer, Map<ResourceType, Integer>> shelves;

    @Override
    public void handle(View view) {
        view.notify(this);
    }

    public Map<ResourceContainer, Map<ResourceType, Integer>> getShelves() {
        return shelves;
    }
}
