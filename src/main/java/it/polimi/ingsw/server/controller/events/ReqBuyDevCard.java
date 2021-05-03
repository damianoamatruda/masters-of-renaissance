package it.polimi.ingsw.server.controller.events;

import it.polimi.ingsw.server.model.DevCardColor;
import it.polimi.ingsw.server.model.resourcecontainers.ResourceContainer;
import it.polimi.ingsw.server.model.resourcetypes.ResourceType;
import it.polimi.ingsw.server.view.View;

import java.util.Map;

public class ReqBuyDevCard implements VCEvent {
    private DevCardColor color;
    private int level;
    private int slotIndex;
    private Map<ResourceContainer, Map<ResourceType, Integer>> resContainers;

    @Override
    public void handle(View view) {
        view.notify(this);
    }

    public DevCardColor getColor() {
        return color;
    }

    public int getLevel() {
        return level;
    }

    public int getSlotIndex() {
        return slotIndex;
    }

    public Map<ResourceContainer, Map<ResourceType, Integer>> getResContainers() {
        return resContainers;
    }
}
