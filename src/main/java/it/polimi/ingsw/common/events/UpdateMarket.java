package it.polimi.ingsw.common.events;

import it.polimi.ingsw.common.ReducedMarket;
import it.polimi.ingsw.common.View;

public class UpdateMarket implements MVEvent {
    private final ReducedMarket market;

    public UpdateMarket(ReducedMarket m) {
        this.market = m;
    }

    public ReducedMarket getMarket() { return market; }

    @Override
    public void handle(View view) {
        // TODO Auto-generated method stub
        
    }
}
