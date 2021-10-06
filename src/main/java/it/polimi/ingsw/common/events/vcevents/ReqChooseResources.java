package it.polimi.ingsw.common.events.vcevents;

import java.util.Map;

/**
 * Client request for resources' setup choice.
 */
public class ReqChooseResources implements VCEvent {
    /** Container-resourcetype-quantity mappings. */
    private final Map<Integer, Map<String, Integer>> shelves;

    /**
     * Class constructor.
     *
     * @param shelves the mapping used when handling the request
     */
    public ReqChooseResources(Map<Integer, Map<String, Integer>> shelves) {
        this.shelves = shelves;
    }

    /**
     * @return the mapping used when handling the request
     */
    public Map<Integer, Map<String, Integer>> getShelves() {
        return shelves;
    }
}
