package it.polimi.ingsw.resourcetypes;

import it.polimi.ingsw.Player;
import it.polimi.ingsw.strongboxes.Strongbox;

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

    // /**
    //  * @return the single instance of this class.
    //  */
    // commented because it can't be overridden (it's static) and
    // because it doesn't make sense to get an instance of an abstract class
    // (-> would return null)
    // public static ResourceType getInstance() { return null; }

    /**
     * @return  the name of the resource associated with the class.
     *          For UI purposes only.
     */
    public abstract String getName();

    /**
     * Routine for producing the resource. Its effect is adding the resource to the player.
     *
     * @param player        the player the resource goes to.
     * @param strongbox     the storage in which the resource is deposited, if applicable.
     * @throws Exception    if it is not possible
     */
    public abstract void onTaken(Player player, Strongbox strongbox) throws Exception;

    /**
     * Routine for consuming the resource. Its effect is removing the resource from the player.
     *
     * @param player        the player the resource is taken from.
     * @param strongbox     the storage from which the resource is removed, if applicable.
     * @throws Exception    if it is not possible
     */
    public abstract void onGiven(Player player, Strongbox strongbox) throws Exception;
}
