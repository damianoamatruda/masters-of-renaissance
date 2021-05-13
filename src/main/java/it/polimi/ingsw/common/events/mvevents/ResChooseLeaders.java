package it.polimi.ingsw.common.events.mvevents;

import it.polimi.ingsw.common.View;

import java.util.List;

/** Server confirmation to the leader choice request during player setup. */
public class ResChooseLeaders implements MVEvent {
    /** The IDs of the chosen leader cards. */
    private final List<Integer> leaders;

    /**
     * Class constructor.
     *
     * @param leaders the IDs of the chosen leader cards
     */
    public ResChooseLeaders(List<Integer> leaders) {
        this.leaders = leaders;
    }

    @Override
    public void handle(View view) {
        view.update(this);
    }

    /**
     * @return the IDs of the chosen leader cards
     */
    public List<Integer> getLeaders() {
        return leaders;
    }
}
