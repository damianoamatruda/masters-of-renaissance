package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.resourcetypes.ResourceType;

import java.util.ArrayList;
import java.util.List;

public class ReducedMarket {
    private final List<List<String>> grid;

    /** The type of the resources that can be replaced. */
    private final String replaceableResType;

    /** The resource in the slide. */
    private String slide;

    public ReducedMarket(Market m) {
        grid = new ArrayList<>();
        for(int i = 0; i < m.getGrid().size(); i++) {
            grid.add(m.getGrid().get(i).stream().map(ResourceType::getName).toList());
        }
        slide = m.getSlide().getName();
        replaceableResType = m.getReplaceableResType().getName();
    }
}
