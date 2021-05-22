package it.polimi.ingsw.client.ViewModel;

import java.util.ArrayList;
import java.util.List;

import it.polimi.ingsw.common.events.vcevents.VCEvent;

public class UIData {
    private final List<VCEvent> requests;

    public UIData() {
        requests = new ArrayList<>();
    }

    /**
     * @return the requests sent by the client
     */
    public List<VCEvent> getRequests() {
        return requests;
    }

    /**
     * @return the last request made to the server
     */
    public VCEvent lastRequest() {
        return requests.get(requests.size() - 1);
    }

    
}
