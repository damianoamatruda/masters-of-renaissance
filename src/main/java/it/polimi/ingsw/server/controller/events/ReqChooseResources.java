package it.polimi.ingsw.server.controller.events;

import it.polimi.ingsw.server.model.resourcecontainers.Shelf;
import it.polimi.ingsw.server.model.resourcetypes.ResourceType;
import it.polimi.ingsw.server.view.View;

import java.util.Map;

public class ReqChooseResources implements VCEvent {
    private Map<Shelf, Map<ResourceType, Integer>> shelves;

    @Override
    public void handle(View view) {
        view.notify(this);
    }

    public Map<Shelf, Map<ResourceType, Integer>> getShelves() {
        return shelves;
    }
}
