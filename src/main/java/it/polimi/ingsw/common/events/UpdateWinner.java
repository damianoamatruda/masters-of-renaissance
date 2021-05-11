package it.polimi.ingsw.common.events;

import java.util.Map;

import it.polimi.ingsw.common.View;

/** Winner player and victory points state update. */
public class UpdateWinner implements MVEvent {
    /** 
     * The nickname of the player who won the game.
     * A null value signifies that the black cross marker is the winner.
     */
    private final String winner;
    /** The victory points of the players. */
    private final Map<String, Integer> victoryPoints;

    /**
     * Class constructor.
     * 
     * @param winner        the nickname of the player who won the game
     *                      A null value signifies that the black cross marker is the winner.
     * @param victoryPoints the victory points of the players
     */
    public UpdateWinner(String winner, Map<String, Integer> victoryPoints) {
        this.winner = winner;
        this.victoryPoints = victoryPoints;
    }
    
    /**
     * @return the nickname of the player who won the game.
     *         A null value signifies that the black cross marker is the winner.
     */
    public String getWinner() {
        return winner;
    }

    /**
     * @return the victory points of the players
     */
    public Map<String, Integer> getVictoryPoints() {
        return victoryPoints;
    }

    @Override
    public void handle(View view) {
    // TODO Auto-generated method stub

    }
}
