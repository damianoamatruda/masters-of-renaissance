package it.polimi.ingsw.common.events;

import it.polimi.ingsw.common.ReducedProductionRequest;
import it.polimi.ingsw.common.View;

import java.util.List;

public class ReqActivateProduction implements VCEvent {
    private final List<ReducedProductionRequest> productionGroup;

    public ReqActivateProduction(List<ReducedProductionRequest> productionGroup) {
        this.productionGroup = productionGroup;
    }

    @Override
    public void handle(View view) {
        view.notify(this);
    }

    public List<ReducedProductionRequest> getProductionGroup() {
        return productionGroup;
    }
}
