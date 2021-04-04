package it.polimi.ingsw.resourcetypes;

import it.polimi.ingsw.strongboxes.Strongbox;

/**
 * Resource that can be substituted with another resource
 */
public class Zero extends ResourceType {
    /**
     * Single instance of the class
     */
    private static ResourceType resource;

    private Zero() { }

    @Override
    public boolean isBlank() { return true; }

    @Override
    public boolean isStorable() { return false; }

    /**
     * @return  the single instance of this class
     */
    public static ResourceType getInstance() {
        if (resource == null) resource = new Zero();
        return resource;
    }

    @Override
    public String getName() { return "zero"; }
}
