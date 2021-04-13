package it.polimi.ingsw.model;

import it.polimi.ingsw.model.leadercards.LeaderCard;

/**
 * Card is the abstract class for all card-like entities that share the victory points attribute.
 *
 * @see DevelopmentCard
 * @see LeaderCard
 */
public abstract class Card {
    /** The amount of points the card gives to its owner. */
    private final int victoryPoints;

    /**
     * Class constructor.
     *
     * @param victoryPoints the amount of victory points associated with the card
     * 
     * @see Game
     * @see Player
     */
    public Card(int victoryPoints) { this.victoryPoints = victoryPoints; }

    /**
     * @return  the amount of victory points set when created.
     */
    public int getVictoryPoints() { return victoryPoints; }
}
