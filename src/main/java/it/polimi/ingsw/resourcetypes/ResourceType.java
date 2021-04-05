package it.polimi.ingsw.resourcetypes;

import it.polimi.ingsw.Game;
import it.polimi.ingsw.Player;
import it.polimi.ingsw.strongboxes.Strongbox;

/**
 * Generic resource archetype.
 */
public abstract class ResourceType {
    /**
     * @return  whether the resource can be replaced with another resource.
     */
    public abstract boolean isBlank();

    /**
     * @return  whether the resource can be stored in a strongbox.
     */
    public abstract boolean isStorable();

    /**
     * @return  the name of the resource associated with the class, for UI purposes only
     */
    public abstract String getName();

    /**
     * Routine for giving the resource to the player. It should be always possible.
     *
     * @param game          the game the player is playing in
     * @param player        the player the resource goes to
     */
    public void giveToPlayer(Game game, Player player) { }

    /**
     * Routine for taking the resource from the player. It should be always possible.
     *
     * @param game          the game the player is playing in
     * @param player        the player the resource is taken from
     */
    public void takeFromPlayer(Game game, Player player) { }

    /**
     * Routine for discarding the resource. It should be always possible.
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
     * Routine for adding the resource into a strongbox.
     *
     * @param strongbox     the storage in which the resource is deposited, if applicable
     * @throws Exception    if it is not possible
     */
    public void addIntoStrongbox(Strongbox strongbox) throws Exception {
        strongbox.addResource(this);
    }

    /**
     * Routine for removing the resource from a strongbox.
     *
     * @param strongbox     the storage from which the resource is removed, if applicable
     * @throws Exception    if it is not possible
     */
    public void removeFromStrongbox(Strongbox strongbox) throws Exception {
        strongbox.removeResource(this);
    }
}
