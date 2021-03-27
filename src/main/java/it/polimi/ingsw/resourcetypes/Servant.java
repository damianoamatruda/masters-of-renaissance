package it.polimi.ingsw.resourcetypes;

import it.polimi.ingsw.Player;
import it.polimi.ingsw.strongboxes.Strongbox;

public class Servant extends ResourceType {
    /**
     * Single instance of the class
     */
    private static ResourceType resource;

    private Servant() { }

    @Override
    public boolean isBlank() { return false; }

    @Override
    public boolean isStorable() { return true; }

    /**
     * @return  the single instance of this class.
     */
    public static ResourceType getInstance() {
        if (resource == null) resource = new Servant();
        return resource;
    }

    @Override
    public String getName() { return "servant"; }

    @Override
    public void onTaken(Player player, Strongbox strongbox) throws Exception {
        strongbox.addResource(Servant.getInstance());
    }

    @Override
    public void onGiven(Player player, Strongbox strongbox) throws Exception {
        strongbox.removeResource(Servant.getInstance());
    }
}
