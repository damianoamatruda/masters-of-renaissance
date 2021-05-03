package it.polimi.ingsw.common.events;

import it.polimi.ingsw.common.View;

import java.util.List;

public class ReqActivateProduction implements VCEvent {
    private final List<Object> productionGroup;

    public ReqActivateProduction(List<Object> productionGroup) {
        this.productionGroup = productionGroup;
    }

    @Override
    public void handle(View view) {
        view.notify(this);
    }

    public Object getProductionGroup() {
        return productionGroup;
    }
}
