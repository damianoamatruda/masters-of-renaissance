package it.polimi.ingsw.model.resourcetypes;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.resourcecontainers.ResourceContainer;

/**
 * Generic resource archetype.
 */
public abstract class ResourceType {
    /**
     * Returns whether the resource type is storable.
     *
     * @return  whether the resource can be stored in a resource container.
     */
    public abstract boolean isStorable();

    /**
     * Returns the name of the resource type.
     *
     * @return  the name of the resource associated with the class, for UI purposes only
     */
    public abstract String getName();

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

    /**
     * Routine for adding a resource of this type into a resource container.
     *
     * @param resContainer     the storage in which the resource is deposited, if applicable
     * @throws Exception    if it is not possible
     */
    public void addIntoContainer(ResourceContainer resContainer) throws Exception {
        resContainer.addResource(this);
    }

    /**
     * Routine for removing a resource of this type from a resource container.
     *
     * @param resContainer     the storage from which the resource is removed, if applicable
     * @throws Exception    if it is not possible
     */
    public void removeFromContainer(ResourceContainer resContainer) throws Exception {
        resContainer.removeResource(this);
    }
}
