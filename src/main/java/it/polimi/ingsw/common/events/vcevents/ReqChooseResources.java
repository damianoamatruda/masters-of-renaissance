package it.polimi.ingsw.common.events.vcevents;

import it.polimi.ingsw.common.View;

import java.util.Map;

/** Client request for setup resource choice. */
public class ReqChooseResources implements VCEvent {
    /** Container-resourcetype-amount mappings. */
    private final Map<Integer, Map<String, Integer>> shelves;

    /**
     * Class constructor.
     * 
     * @param shelves the mapping used when handling the request
     */
    public ReqChooseResources(Map<Integer, Map<String, Integer>> shelves) {
        this.shelves = shelves;
    }

    @Override
    public void handle(View view) {
        view.notify(this);
    }

    /**
     * @return the mapping used when handling the request
     */
    public Map<Integer, Map<String, Integer>> getShelves() {
        return shelves;
    }
}
