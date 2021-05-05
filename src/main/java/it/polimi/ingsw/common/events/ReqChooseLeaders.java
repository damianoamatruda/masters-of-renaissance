package it.polimi.ingsw.common.events;

import it.polimi.ingsw.common.ControllerObservable;

import java.util.List;

public class ReqChooseLeaders implements VCEvent {
    private final List<Integer> leaders;

    public ReqChooseLeaders(List<Integer> leaders) {
        this.leaders = leaders;
    }

    @Override
    public void handle(ControllerObservable view) {
        view.notify(this);
    }

    public List<Integer> getLeaders() {
        return leaders;
    }
}
