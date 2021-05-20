package it.polimi.ingsw.common.reducedmodel;

import java.util.Map;

public class ReducedResourceContainer {
    /** The ID of the modified container. */
    private final int id;

    /** The new state of the container. */
    private final Map<String, Integer> content;

    /** The resource binding the container's contents. */
    private final String boundedResType;

    private final int dimensions;

    /**
     * Class constructor.
     *
     * @param id             the ID of the modified container
     * @param content        the new state of the container
     * @param boundedResType the resource binding the container's contents
     */
    public ReducedResourceContainer(int id, int dimensions, Map<String, Integer> content, String boundedResType) {
        this.id = id;
        this.dimensions = dimensions;
        this.content = content;
        this.boundedResType = boundedResType;
    }

    /**
     * @return the dimensions
     */
    public int getDimensions() {
        return dimensions;
    }

    /**
     * @return the ID of the modified container
     */
    public int getId() {
        return id;
    }

    /**
     * @return the new state of the container
     */
    public Map<String, Integer> getContent() {
        return content;
    }

    /**
     * @return the resource binding the container's contents
     */
    public String getboundedResType() {
        return boundedResType;
    }
}
