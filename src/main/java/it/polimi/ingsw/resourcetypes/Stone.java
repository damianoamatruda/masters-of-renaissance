package it.polimi.ingsw.resourcetypes;

import it.polimi.ingsw.Player;
import it.polimi.ingsw.strongboxes.Strongbox;

public class Stone extends ResourceType {
    /**
     * Single instance of the class
     */
    private static ResourceType resource;

    private Stone() { }

    @Override
    public boolean isBlank() { return false; }

    /**
     * @return  the single instance of this class.
     */
    public static ResourceType getInstance() {
        if (resource == null) resource = new Stone();
        return resource;
    }

    @Override
    public String getName() { return "stone"; }

    @Override
    public void onTaken(Player player, Strongbox strongbox) {
        strongbox.addResource(Stone.getInstance());
    }
}
