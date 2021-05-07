package it.polimi.ingsw.common.events;

import java.util.List;

import it.polimi.ingsw.common.View;

public class UpdateDevGrid implements MVEvent {
    private final List<List<Integer>> topCards;

    public UpdateDevGrid(List<List<Integer>> topCards) {
        this.topCards = topCards;
    }

    public List<List<Integer>> getCards() {
        return topCards;
    }

    @Override
    public void handle(View view) {
        // TODO Auto-generated method stub
        
    }
}
