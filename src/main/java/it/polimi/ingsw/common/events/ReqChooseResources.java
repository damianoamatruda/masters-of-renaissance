package it.polimi.ingsw.common.events;

import it.polimi.ingsw.common.View;

import java.util.Map;

public class ReqChooseResources implements VCEvent {
    private final Map<Integer, Map<String, Integer>> shelves;

    public ReqChooseResources(Map<Integer, Map<String, Integer>> shelves) {
        this.shelves = shelves;
    }

    @Override
    public void handle(View view) {
        view.notify(this);
    }

    public Map<Integer, Map<String, Integer>> getShelves() {
        return shelves;
    }
}
