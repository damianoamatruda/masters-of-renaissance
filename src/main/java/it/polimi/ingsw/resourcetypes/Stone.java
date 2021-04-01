package it.polimi.ingsw.resourcetypes;

import it.polimi.ingsw.Player;
import it.polimi.ingsw.strongboxes.Strongbox;

/**
 * Resource representing a stone.
 */
public class Stone extends ResourceType {
    /**
     * Single instance of the class
     */
    private static ResourceType resource;

    private Stone() { }

    @Override
    public boolean isBlank() { return false; }

    @Override
    public boolean isStorable() { return true; }

    /**
     * @return  the single instance of this class.
     */
    public static ResourceType getInstance() {
        if (resource == null) resource = new Stone();
        return resource;
    }

    @Override
    public String getName() { return "stone"; }

}
