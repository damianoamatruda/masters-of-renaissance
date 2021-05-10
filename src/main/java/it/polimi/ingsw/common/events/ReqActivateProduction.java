package it.polimi.ingsw.common.events;

import it.polimi.ingsw.common.ReducedProductionRequest;
import it.polimi.ingsw.common.View;

import java.util.List;

public class ReqActivateProduction implements VCEvent {
    private final List<ReducedProductionRequest> transactionRequests;

    public ReqActivateProduction(List<ReducedProductionRequest> transactionRequests) {
        this.transactionRequests = transactionRequests;
    }

    @Override
    public void handle(View view) {
        view.notify(this);
    }

    public List<ReducedProductionRequest> getProductionRequests() {
        return transactionRequests;
    }
}
