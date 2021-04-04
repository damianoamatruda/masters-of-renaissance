package it.polimi.ingsw.resourcetypes;

import it.polimi.ingsw.Game;
import it.polimi.ingsw.Player;
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

    @Override
    public void giveToPlayer(Game game, Player player, Strongbox strongbox) throws Exception { }

    @Override
    public void takeFromPlayer(Game game, Player player, Strongbox strongbox) throws Exception { }
}
