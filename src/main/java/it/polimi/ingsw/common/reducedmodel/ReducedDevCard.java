package it.polimi.ingsw.common.reducedmodel;

public class ReducedDevCard {
    private final String color;
    private final ReducedCardRequirement cost;
    private final int level;
    private final int productionId;
    
    /**
     * @param color
     * @param cost
     * @param level
     * @param productionId
     */
    public ReducedDevCard(String color, ReducedCardRequirement cost, int level, int productionId) {
        this.color = color;
        this.cost = cost;
        this.level = level;
        this.productionId = productionId;
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
     * @return the productionId of the card's production
     */
    public int getProductionId() {
        return productionId;
    }
}
