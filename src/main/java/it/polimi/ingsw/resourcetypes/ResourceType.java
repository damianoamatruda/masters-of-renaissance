package it.polimi.ingsw.resourcetypes;

import it.polimi.ingsw.Game;
import it.polimi.ingsw.Player;
import it.polimi.ingsw.strongboxes.Strongbox;

/**
 * Generic resource archetype.
 */
public abstract class ResourceType {
    // /**
    //  * Single instance of the class
    //  */
    // commented as it creates conflicts when using subclasses
    // protected static ResourceType resource;

    /**
     * Class constructor.
     */
    protected ResourceType() { }

    /**
     * @return  whether the resource can be replaced with another resource.
     */
    public abstract boolean isBlank();

    /**
     * @return  whether the resource can be stored in a strongbox.
     */
    public abstract boolean isStorable();

    /**
     * @return  the name of the resource associated with the class.
     *          For UI purposes only.
     */
    public abstract String getName();

    /**
     * Routine for giving the resource to the player.
     *
     * @param game          the game the player is playing in
     * @param player        the player the resource goes to
     * @param strongbox     the storage in which the resource is deposited, if applicable
     * @throws Exception    if it is not possible
     */
    public void onGiven(Game game, Player player, Strongbox strongbox) throws Exception {
        strongbox.addResource(this);
    }

    /**
     * Routine for taking the resource from the player.
     *
     * @param game          the game the player is playing in
     * @param player        the player the resource is taken from
     * @param strongbox     the storage from which the resource is removed, if applicable
     * @throws Exception    if it is not possible
     */
    public void onTaken(Game game, Player player, Strongbox strongbox) throws Exception {
        strongbox.removeResource(this);
    }

    /**
     * Routine for discarding the resource.
     *
     * @param game          the game the player is playing in
     * @param player        the player discarding the resource
     * @param strongbox     the storage from which the resource is discarded
     * @throws Exception    if it is not possible
     */
    public void onDiscard(Game game, Player player, Strongbox strongbox) throws Exception {
        game.getPlayers().stream()
                .filter(p -> !p.equals(player))
                .forEach(p -> p.incrementFaithPoints(game));
    }
}
