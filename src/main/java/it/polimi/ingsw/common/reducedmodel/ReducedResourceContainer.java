package it.polimi.ingsw.common.reducedmodel;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ReducedResourceContainer {
    /** The ID of the modified container. */
    private final int id;

    /** The new state of the container. */
    private final Map<String, Integer> content;

    /** The resource binding the container's contents. */
    private final String boundedResType;

    private final int size;

    /**
     * Class constructor.
     *
     * @param id             the ID of the modified container
     * @param content        the new state of the container
     * @param boundedResType the resource binding the container's contents
     */
    public ReducedResourceContainer(int id, int size, Map<String, Integer> content, String boundedResType) {
        if (content == null)
            content = new HashMap<>();

        this.id = id;
        this.size = size;
        this.content = new HashMap<>(content);
        this.boundedResType = boundedResType;
    }

    /**
     * @return the size
     */
    public int getSize() {
        return size;
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
    public Optional<String> getBoundedResType() {
        return Optional.ofNullable(boundedResType);
    }
}
