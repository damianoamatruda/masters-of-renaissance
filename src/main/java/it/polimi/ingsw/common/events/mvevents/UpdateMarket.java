package it.polimi.ingsw.common.events.mvevents;

import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.reducedmodel.ReducedMarket;

/** Market's state update. */
public class UpdateMarket extends ViewEvent {
    /** The reducedmarket containing the new market status. */
    private final ReducedMarket market;

    /**
     * Class constructor.
     *
     * @param market the reducedmarket containing the new market status
     */
    public UpdateMarket(View view, ReducedMarket market) {
        super(view);
        this.market = market;
    }

    public UpdateMarket(ReducedMarket market) {
        this(null, market);
    }

    /**
     * @return the reducedmarket containing the new market status
     */
    public ReducedMarket getMarket() {
        return this.market;
    }
}
