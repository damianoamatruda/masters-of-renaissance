package it.polimi.ingsw.common.reducedmodel;

public class ReducedDevCard {
    private final int id;
    private final String color;
    private final ReducedCardRequirement cost;
    private final int level;
    private final int production;
    private final int victoryPoints;

    /**
     * @param color
     * @param cost
     * @param level
     * @param production
     */
    public ReducedDevCard(int id, int victoryPoints, String color, ReducedCardRequirement cost, int level, int production) {
        this.id = id;
        this.victoryPoints = victoryPoints;
        this.color = color;
        this.cost = cost;
        this.level = level;
        this.production = production;
    }

    /**
     * @return the victoryPoints
     */
    public int getVictoryPoints() {
        return victoryPoints;
    }

    /**
     * @return the id of the card
     */
    public int getId() {
        return id;
    }

    /**
     * @return the color of the card
     */
    public String getColor() {
        return color;
    }

    /**
     * @return the cost of the card
     */
    public ReducedCardRequirement getCost() {
        return cost;
    }

    /**
     * @return the level of the card
     */
    public int getLevel() {
        return level;
    }

    /**
     * @return the ID of the production of the card
     */
    public int getProduction() {
        return production;
    }
}
