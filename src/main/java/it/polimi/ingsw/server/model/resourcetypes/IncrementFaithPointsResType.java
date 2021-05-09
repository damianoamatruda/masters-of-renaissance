package it.polimi.ingsw.server.model.resourcetypes;

import it.polimi.ingsw.server.model.Game;
import it.polimi.ingsw.server.model.Player;

/**
 * This class represents a resource type that, when given to a player, increments its faith points.
 */
public class IncrementFaithPointsResType extends ResourceType {
    /**
     * Class constructor.
     *
     * @param name     the name of the resource type
     * @param storable <code>true</code> if the resources of this type can be stored in a resource container;
     *                 <code>false</code> otherwise.
     */
    public IncrementFaithPointsResType(String name, boolean storable) {
        super(name, storable);
    }

    @Override
    public boolean isGiveableToPlayer() {
        return true;
    }

    @Override
    public void giveToPlayer(Game game, Player player) {
        player.incrementFaithPoints(game, 1);
    }
}
