package it.polimi.ingsw.common.backend.model;

import it.polimi.ingsw.common.EventDispatcher;
import it.polimi.ingsw.common.backend.model.leadercards.LeaderCard;

/**
 * Card is the abstract class for all card-like entities that share the victory points attribute.
 *
 * @see DevelopmentCard
 * @see LeaderCard
 */
public abstract class Card extends EventDispatcher {
    private final int id;

    /** The quantity of points the card gives to its owner. */
    private final int victoryPoints;

    /**
     * Class constructor.
     *
     * @param victoryPoints the quantity of victory points associated with the card
     * @see Game
     * @see Player
     */
    public Card(int victoryPoints, int id) {
        this.victoryPoints = victoryPoints;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    /**
     * @return the quantity of victory points set when created.
     */
    public int getVictoryPoints() {
        return victoryPoints;
    }
}
