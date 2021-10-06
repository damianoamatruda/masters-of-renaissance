package it.polimi.ingsw.common.events.vcevents;

import java.util.List;

/**
 * Client request for setup leader choice.
 */
public class ReqChooseLeaders implements VCEvent {
    /** The IDs of the chosen leader cards. */
    private final List<Integer> leaders;

    /**
     * Class constructor.
     *
     * @param leaders the IDs of the chosen leader cards
     */
    public ReqChooseLeaders(List<Integer> leaders) {
        this.leaders = leaders;
    }

    /**
     * @return the IDs of the chosen leader cards
     */
    public List<Integer> getLeaders() {
        return leaders;
    }
}
