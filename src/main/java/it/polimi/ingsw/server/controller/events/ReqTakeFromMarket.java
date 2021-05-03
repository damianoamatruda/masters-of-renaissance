package it.polimi.ingsw.server.controller.events;

import it.polimi.ingsw.server.model.resourcecontainers.Shelf;
import it.polimi.ingsw.server.model.resourcetypes.ResourceType;
import it.polimi.ingsw.server.view.View;

import java.util.Map;

public class ReqTakeFromMarket implements VCEvent {
    private boolean isRow;
    private int index;
    private Map<ResourceType, Integer> replacements;
    private Map<Shelf, Map<ResourceType, Integer>> shelves;

    @Override
    public void handle(View view) {
        view.notify(this);
    }

    public boolean isRow() {
        return isRow;
    }

    public int getIndex() {
        return index;
    }

    public Map<ResourceType, Integer> getReplacements() {
        return replacements;
    }

    public Map<Shelf, Map<ResourceType, Integer>> getShelves() {
        return shelves;
    }
}
