package it.polimi.ingsw.common.reducedmodel;

public abstract class ReducedCard {
    protected final int id;
    protected final int victoryPoints;
    protected final int production;

    public ReducedCard(int id, int victoryPoints, int production) {
        this.id = id;
        this.victoryPoints = victoryPoints;
        this.production = production;
    }

    /**
     * @return the id of the card
     */
    public int getId() {
        return id;
    }

    /**
     * @return the victoryPoints of the card
     */
    public int getVictoryPoints() {
        return victoryPoints;
    }

    /**
     * @return the ID of the production of the card
     */
    public int getProduction() {
        return production;
    }
}
