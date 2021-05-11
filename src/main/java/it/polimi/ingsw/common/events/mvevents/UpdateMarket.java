package it.polimi.ingsw.common.events.mvevents;

import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.reducedmodel.ReducedMarket;

/** Market's state update. */
public class UpdateMarket implements MVEvent {
    /** The reducedmarket containing the new market status. */
    private final ReducedMarket market;

    /**
     * Class constructor.
     *
     * @param market the reducedmarket containing the new market status
     */
    public UpdateMarket(ReducedMarket market) {
        this.market = market;
    }

    @Override
    public void handle(View view) {
        view.update(this);
    }

    /**
     * @return the reducedmarket containing the new market status
     */
    public ReducedMarket getMarket() {
        return this.market;
    }
}
