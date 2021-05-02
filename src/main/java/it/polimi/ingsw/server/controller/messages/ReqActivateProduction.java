package it.polimi.ingsw.server.controller.messages;

import it.polimi.ingsw.server.model.ProductionGroup;
import it.polimi.ingsw.server.view.View;

public class ReqActivateProduction implements Message {
    ProductionGroup productionGroup;

    @Override
    public void handle(View view) {
        view.notify(this);
    }

    public ProductionGroup getProductionGroup() {
        return productionGroup;
    }
}
