package it.polimi.ingsw.common.events;

import it.polimi.ingsw.common.ControllerObservable;

import java.util.List;

public class ReqActivateProduction implements VCEvent {
    private final List<Object> productionGroup;

    public ReqActivateProduction(List<Object> productionGroup) {
        this.productionGroup = productionGroup;
    }

    @Override
    public void handle(ControllerObservable view) {
        view.notify(this);
    }

    public Object getProductionGroup() {
        return productionGroup;
    }
}
