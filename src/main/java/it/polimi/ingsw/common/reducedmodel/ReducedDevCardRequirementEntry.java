package it.polimi.ingsw.common.reducedmodel;

public class ReducedDevCardRequirementEntry {
    private final String color;
    private final int quantity;
    private final int level;

    /**
     * @param color    the color of the required card
     * @param level    the level of the required card
     * @param quantity the quantity of cards required
     */
    public ReducedDevCardRequirementEntry(String color, int level, int quantity) {
        if (color == null)
            throw new IllegalArgumentException("Null color constructing reduced dev card requirement entry");

        this.color = color;
        this.level = level;
        this.quantity = quantity;
    }

    /**
     * @return the color
     */
    public String getColor() {
        return color;
    }

    /**
     * @return the quantity
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * @return the level
     */
    public int getLevel() {
        return level;
    }
}   