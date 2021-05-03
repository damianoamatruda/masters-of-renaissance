package it.polimi.ingsw.server.controller.events;

import it.polimi.ingsw.server.model.ProductionGroup;
import it.polimi.ingsw.server.view.View;

public class ReqActivateProduction implements VCEvent {
    ProductionGroup productionGroup;

    @Override
    public void handle(View view) {
        view.notify(this);
    }

    public ProductionGroup getProductionGroup() {
        return productionGroup;
    }
}
