package it.polimi.ingsw.model.resourcetypes;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.resourcecontainers.ResourceContainer;

/**
 * Generic resource archetype.
 */
public class ResourceType {
    /** The name of the resource type. */
    private final String name;

    /** <code>true</code> if the resource can be stored in a resource container; <code>false</code> otherwise. */
    private final boolean storable;

    public ResourceType(String name, boolean storable) {
        this.name = name;
        this.storable = storable;
    }

    /**
     * Returns whether the resource type is storable.
     *
     * @return  whether the resource can be stored in a resource container.
     */
    public boolean isStorable() {
        return storable;
    }

    /**
     * Returns the name of the resource type.
     *
     * @return  the name of the resource associated with the class, for UI purposes only
     */
    public String getName() {
        return name;
    }

    /**
     * Routine for giving a resource of this type to the player. This should be always possible.
     *
     * @param game          the game the player is playing in
     * @param player        the player the resource goes to
     */
    public void giveToPlayer(Game game, Player player) { }

    /**
     * Routine for taking a resource of this type from the player. This should be always possible.
     *
     * @param game          the game the player is playing in
     * @param player        the player the resource is taken from
     */
    public void takeFromPlayer(Game game, Player player) { }

    /**
     * Routine for discarding a resource of this type. This should be always possible.
     *
     * @param game      the game the player is playing in
     * @param player    the player discarding the resource
     */
    public void discard(Game game, Player player) {
        game.getPlayers().stream()
                .filter(p -> !p.equals(player))
                .forEach(p -> p.incrementFaithPoints(game));
    }
}
