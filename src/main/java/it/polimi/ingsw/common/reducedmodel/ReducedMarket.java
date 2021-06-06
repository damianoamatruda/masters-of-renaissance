package it.polimi.ingsw.common.reducedmodel;

import java.util.List;

public class ReducedMarket {
    private final List<List<String>> grid;

    /** The type of the resources that can be replaced. */
    private final String replaceableResType;

    /** The resource in the slide. */
    private final String slide;

    public ReducedMarket(List<List<String>> grid, String replaceableResType, String slide) {
        this.grid = grid;
        this.replaceableResType = replaceableResType;
        this.slide = slide;
    }

    /**
     * @return the slide's resource
     */
    public String getSlide() {
        return slide;
    }

    /**
     * @return the replaceable resource type
     */
    public String getReplaceableResType() {
        return replaceableResType;
    }

    /**
     * @return the grid
     */
    public List<List<String>> getGrid() {
        return grid;
    }
}
