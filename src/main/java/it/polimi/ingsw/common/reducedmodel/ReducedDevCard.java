package it.polimi.ingsw.common.reducedmodel;

import java.util.Optional;

public class ReducedDevCard extends ReducedCard {
    private final String color;
    private final ReducedResourceRequirement cost;
    private final int level;

    /**
     * @param color
     * @param cost
     * @param level
     * @param production
     */
    public ReducedDevCard(int id, int victoryPoints, String color, ReducedResourceRequirement cost, int level, int production) {
        super(id, victoryPoints, production);

        this.color = color;
        this.cost = cost;
        this.level = level;
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
    public Optional<ReducedResourceRequirement> getCost() {
        return Optional.ofNullable(cost);
    }

    /**
     * @return the level of the card
     */
    public int getLevel() {
        return level;
    }

}
