package it.polimi.ingsw.resourcetypes;

import it.polimi.ingsw.Player;
import it.polimi.ingsw.strongboxes.Strongbox;

public class Faith extends ResourceType {
    /**
     * Single instance of the class
     */
    private static ResourceType resource;

    private Faith() { }

    @Override
    public boolean isBlank() { return false; }

    @Override
    public boolean isStorable() { return false; }

    /**
     * @return  the single instance of this class.
     */
    public static ResourceType getInstance() {
        if (resource == null) resource = new Faith();
        return resource;
    }

    @Override
    public String getName() { return "faith"; }

    @Override
    public void onGiven(Player player, Strongbox strongbox) {
        player.incrementFaithPoints();
    }

    @Override
    public void onTaken(Player player, Strongbox strongbox) throws Exception { }
}
