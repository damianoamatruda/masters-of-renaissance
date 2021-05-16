package it.polimi.ingsw.common.backend.model;

import it.polimi.ingsw.common.EventEmitter;
import it.polimi.ingsw.common.backend.model.leadercards.LeaderCard;
import it.polimi.ingsw.common.events.mvevents.UpdateLeader;

import java.util.Set;

/**
 * Card is the abstract class for all card-like entities that share the victory points attribute.
 *
 * @see DevelopmentCard
 * @see LeaderCard
 */
public abstract class Card extends EventEmitter {
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
    public Card(int victoryPoints, int id) {
        super(Set.of(UpdateLeader.class));
        this.victoryPoints = victoryPoints;
        this.id = id;
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
