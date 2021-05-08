package it.polimi.ingsw.common.events;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.polimi.ingsw.common.View;
import it.polimi.ingsw.server.model.Player;

public class UpdateWinner implements MVEvent {
    private final String winner;
    private final Map<String, Integer> victoryPoints;

    public UpdateWinner(String winner, Map<String, Integer> victoryPoints) {
        this.winner = winner;
        this.victoryPoints = victoryPoints;
    }

    public static Map<String, Integer> computeVictoryPointsMap(List<Player> players) {
        Map<String, Integer> vp = new HashMap<>();
        
        for (Player p : players)
        vp.put(p.getNickname(), p.getVictoryPoints());
        
        return vp;
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
