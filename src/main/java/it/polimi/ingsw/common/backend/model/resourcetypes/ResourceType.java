package it.polimi.ingsw.common.backend.model.resourcetypes;

import it.polimi.ingsw.common.backend.model.Game;
import it.polimi.ingsw.common.backend.model.Player;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceType;

/**
 * This class represents a resource type.
 */
public class ResourceType {
    /** The name of the resource type. */
    private final String name;

    /** The string value of the resource color */
    private final String colorValue;

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
        this.colorValue = "#ffffff";
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
     * Returns whether the resource type is storable.
     *
     * @return <code>true</code> if the resources of this type can be stored in a resource container; <code>false</code>
     * otherwise.
     */
    public final boolean isStorable() {
        return storable;
    }

    public String getColorValue() {
        return colorValue;
    }

    /**
     * Returns whether the resources of this type are giveable to a player.
     *
     * @return <code>true</code> if the resources of this type can be given to a player; <code>false</code> otherwise.
     */
    public boolean isGiveableToPlayer() {
        return false;
    }

    /**
     * Returns whether the resources of this type are takeable from a player.
     *
     * @return <code>true</code> if the resources of this type can be taken from a player; <code>false</code> otherwise.
     */
    public boolean isTakeableFromPlayer() {
        return false;
    }

    /**
     * Routine for giving non-storable resources of this type to the player.
     *
     * @param game     the game the player is playing in
     * @param player   the player the resource is given to
     * @param quantity the quantity of resources to give
     */
    public void giveToPlayer(Game game, Player player, int quantity) {
    }

    /**
     * Routine for taking non-storable resources of this type from the player.
     *
     * @param game     the game the player is playing in
     * @param player   the player the resource is taken from
     * @param quantity the quantity of resources to take
     */
    @SuppressWarnings({"EmptyMethod", "unused"})
    public void takeFromPlayer(Game game, Player player, int quantity) {
    }

    public ReducedResourceType reduce() {
        return new ReducedResourceType(name, colorValue, storable, isGiveableToPlayer(), isTakeableFromPlayer());
    }
}
