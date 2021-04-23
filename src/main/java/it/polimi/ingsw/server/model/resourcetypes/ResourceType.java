package it.polimi.ingsw.server.model.resourcetypes;

import it.polimi.ingsw.server.model.Game;
import it.polimi.ingsw.server.model.Player;

/**
 * Generic resource archetype.
 */
public class ResourceType {
    /** The name of the resource type. */
    private final String name;

    /** <code>true</code> if the resource can be stored in a resource container; <code>false</code> otherwise. */
    private final boolean storable;

    /**
     * Class constructor.
     *
     * @param name     the name of the resource being created.
     * @param storable whether the resource can be stored on a <code>Shelf</code>
     */
    public ResourceType(String name, boolean storable) {
        this.name = name;
        this.storable = storable;
    }

    /**
     * Returns whether the resource type is storable.
     *
     * @return whether the resource can be stored in a resource container.
     */
    public boolean isStorable() {
        return storable;
    }

    /**
     * Returns the name of the resource type.
     *
     * @return the name of the resource associated with the class, for UI purposes only
     */
    public String getName() {
        return name;
    }

    /**
     * Routine for giving a resource of this type to the player.
     *
     * @param game   the game the player is playing in
     * @param player the player the resource goes to
     */
    public void giveToPlayer(Game game, Player player) {
    }

    /**
     * Routine for taking a resource of this type from the player.
     *
     * @param game   the game the player is playing in
     * @param player the player the resource is taken from
     */
    @SuppressWarnings({"EmptyMethod", "unused"})
    public void takeFromPlayer(Game game, Player player) {
    }
}
