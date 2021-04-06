package it.polimi.ingsw.resourcetypes;

import it.polimi.ingsw.Game;
import it.polimi.ingsw.Player;

/**
 * Resource representing a faith point.
 */
public class Faith extends ResourceType {
    /** The name of the resource type. */
    private static final String NAME = "Faith";

    /** The single instance of the class. */
    private static ResourceType instance;

    private Faith() { }

    @Override
    public boolean isBlank() { return false; }

    @Override
    public boolean isStorable() { return false; }

    /**
     * @return  the single instance of the class
     */
    public static ResourceType getInstance() {
        if (instance == null) instance = new Faith();
        return instance;
    }

    @Override
    public String getName() { return NAME; }

    @Override
    public void giveToPlayer(Game game, Player player) {
        player.incrementFaithPoints(game);
    }

    @Override
    public String toString() { return NAME; }
}
