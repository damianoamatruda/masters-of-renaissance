package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.leadercards.LeaderCard;

/**
 * Card is the abstract class for all card-like entities that share the victory points attribute.
 *
 * @see DevelopmentCard
 * @see LeaderCard
 */
public abstract class Card {
    private final int id;
    /** The amount of points the card gives to its owner. */
    private final int victoryPoints;

    /**
     * Class constructor.
     *
     * @param victoryPoints the amount of victory points associated with the card
     * @see Game
     * @see Player
     */
    public Card(int victoryPoints) {
        this.victoryPoints = victoryPoints;
        id = 69;
    }

    public int getId() {
        return id;
    }

    /**
     * @return the amount of victory points set when created.
     */
    public int getVictoryPoints() {
        return victoryPoints;
    }
}
