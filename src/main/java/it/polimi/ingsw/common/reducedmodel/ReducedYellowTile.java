package it.polimi.ingsw.common.reducedmodel;

public class ReducedYellowTile {
    /** The faith points in the Faith Track where the Yellow Tile is. */
    private final int faithPoints;

    /** The bonus progressive victory points earned. */
    private final int victoryPoints;

    /**
     * @param faithPoints   the progressive tile number
     * @param victoryPoints the bonus victory points
     */
    public ReducedYellowTile(int faithPoints, int victoryPoints) {
        this.faithPoints = faithPoints;
        this.victoryPoints = victoryPoints;
    }

    /**
     * @return the victoryPoints
     */
    public int getVictoryPoints() {
        return victoryPoints;
    }

    /**
     * @return the faithPoints
     */
    public int getFaithPoints() {
        return faithPoints;
    }

}
