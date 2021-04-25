package it.polimi.ingsw.server.model.resourcetypes;

import it.polimi.ingsw.server.model.Game;
import it.polimi.ingsw.server.model.Player;

/**
 * This class represents a resource type.
 */
public class ResourceType {
    /** The name of the resource type. */
    private final String name;

    /**
     * <code>true</code> if the resources of this type can be stored in a resource container; <code>false</code>
     * otherwise.
     */
    private final boolean storable;

    /**
     * Class constructor.
     *
     * @param name     the name of the resource type
     * @param storable <code>true</code> if the resources of this type can be stored in a resource container;
     *                 <code>false</code> otherwise.
     */
    public ResourceType(String name, boolean storable) {
        this.name = name;
        this.storable = storable;
    }

    /**
     * Returns whether the resource type is storable.
     *
     * @return <code>true</code> if the resources of this type can be stored in a resource container; <code>false</code> otherwise.
     */
    public boolean isStorable() {
        return storable;
    }

    /**
     * Returns the name of the resource type.
     *
     * @return the name of the resource type
     */
    public String getName() {
        return name;
    }

    /**
     * Routine for giving a non-storable resource of this type to the player.
     *
     * @param game   the game the player is playing in
     * @param player the player the resource is given to
     */
    public void giveToPlayer(Game game, Player player) {
    }

    /**
     * Routine for taking a non-storable resource of this type from the player.
     *
     * @param game   the game the player is playing in
     * @param player the player the resource is taken from
     */
    @SuppressWarnings({"EmptyMethod", "unused"})
    public void takeFromPlayer(Game game, Player player) {
    }

    @Override
    public String toString() {
        return name;
    }
}
