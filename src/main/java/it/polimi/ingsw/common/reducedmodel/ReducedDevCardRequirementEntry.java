package it.polimi.ingsw.common.reducedmodel;

public class ReducedDevCardRequirementEntry {
    private final String color;
    private final int amount;
    private final int level;
 
    /**
     * @param color
     * @param level
     * @param amount
     */
    public ReducedDevCardRequirementEntry(String color, int level, int amount) {
        this.color = color;
        this.level = level;
        this.amount = amount;
    }

    /**
     * @return the color
     */
    public String getColor() {
        return color;
    }

    /**
     * @return the amount
     */
    public int getAmount() {
        return amount;
    }

    /**
     * @return the level
     */
    public int getLevel() {
        return level;
    }
}   