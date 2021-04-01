package it.polimi.ingsw.resourcetypes;

import it.polimi.ingsw.Player;
import it.polimi.ingsw.strongboxes.Strongbox;

/**
 * Resource representing a coin.
 */
public class Coin extends ResourceType {
    /**
     * Single instance of the class
     */
    private static ResourceType resource;

    private Coin() { }

    @Override
    public boolean isBlank() { return false; }

    @Override
    public boolean isStorable() { return true; }

    /**
     * @return  the single instance of this class.
     */
    public static ResourceType getInstance() {
        if (resource == null) resource = new Coin();
        return resource;
    }

    @Override
    public String getName() { return "coin"; }

}
