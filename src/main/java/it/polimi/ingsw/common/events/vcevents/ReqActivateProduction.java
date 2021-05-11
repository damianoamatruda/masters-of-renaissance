package it.polimi.ingsw.common.events.vcevents;

import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.events.vcevents.VCEvent;
import it.polimi.ingsw.common.reducedmodel.ReducedProductionRequest;

import java.util.List;

/** Client request for production activation. */
public class ReqActivateProduction implements VCEvent {
    /** The requests to be activated and the options of their activation. */
    private final List<ReducedProductionRequest> transactionRequests;

    /**
     * Class constructor.
     * 
     * @param transactionRequests the requests to be activated and the options of their activation
     */
    public ReqActivateProduction(List<ReducedProductionRequest> transactionRequests) {
        this.transactionRequests = transactionRequests;
    }

    @Override
    public void handle(View view) {
        view.notify(this);
    }

    /**
     * @return the requests to be activated and the options of their activation
     */
    public List<ReducedProductionRequest> getProductionRequests() {
        return transactionRequests;
    }
}
