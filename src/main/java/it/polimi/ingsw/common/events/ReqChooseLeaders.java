package it.polimi.ingsw.common.events;

import it.polimi.ingsw.common.View;

import java.util.List;

public class ReqChooseLeaders implements VCEvent {
    private final List<Integer> leaders;

    public ReqChooseLeaders(List<Integer> leaders) {
        this.leaders = leaders;
    }

    @Override
    public void handle(View view) {
        view.notify(this);
    }

    public List<Integer> getLeaders() {
        return leaders;
    }
}
