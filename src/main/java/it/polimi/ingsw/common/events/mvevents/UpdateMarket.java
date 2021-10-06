package it.polimi.ingsw.common.events.mvevents;

import it.polimi.ingsw.common.reducedmodel.ReducedMarket;

/**
 * Market's state update.
 */
public class UpdateMarket implements MVEvent {
    /** The reduced market containing the new market status. */
    private final ReducedMarket market;

    /**
     * Class constructor.
     *
     * @param market the reduced market containing the new market status
     */
    public UpdateMarket(ReducedMarket market) {
        this.market = market;
    }

    /**
     * @return the reduced market containing the new market status
     */
    public ReducedMarket getMarket() {
        return this.market;
    }
}
