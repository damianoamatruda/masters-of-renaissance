package it.polimi.ingsw.common.events;

import java.util.Map;

import it.polimi.ingsw.common.View;

public class UpdateWinner implements MVEvent {
    private final String winner;
    private final Map<String, Integer> victoryPoints;

    public UpdateWinner(String winner, Map<String, Integer> victoryPoints) {
        this.winner = winner;
        this.victoryPoints = victoryPoints;
    }
    
    public String getWinner() {
        return winner;
    }

    public Map<String, Integer> getVictoryPoints() {
        return victoryPoints;
    }

    @Override
    public void handle(View view) {
    // TODO Auto-generated method stub

    }
}
