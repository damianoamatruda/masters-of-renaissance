package it.polimi.ingsw;

import it.polimi.ingsw.devcardcolors.DevCardColor;

public class DevelopmentCard extends Card {
    private final DevCardColor color;
    private final ResourceRequirement cost;
    private final int level;
    private final Production production;

    /**
     * Class constructor.
     *
     * @param color         the color of the card.
     * @param level         the level of the card.
     * @param cost          the resources needed to buy the card.
     * @param production    the production associated with the card.
     * @param victoryPoints the victory points associated with the card.
     */
    public DevelopmentCard(DevCardColor color, int level, ResourceRequirement cost, Production production, int victoryPoints) {
        super(victoryPoints);
        this.color = color;
        this.cost = cost;
        this.level = level;
        this.production = production;
    }

    /**
     * @return  the card's color.
     */
    public DevCardColor getColor() { return color; }

    /**
     * @return  the card's cost.
     */
    public ResourceRequirement getCost() { return cost; }

    /**
     * @return  the card's level.
     */
    public int getLevel() { return level; }

    /**
     * @return  the card's production.
     */
    public Production getProduction() { return production; }

    /**
     * @param player the player taking the card.
     */
    public void onTaken(Player player) {
        //TODO implement
    }
}
