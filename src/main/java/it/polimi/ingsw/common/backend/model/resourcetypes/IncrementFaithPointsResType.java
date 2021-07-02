package it.polimi.ingsw.common.backend.model.resourcetypes;

import it.polimi.ingsw.common.backend.model.Game;
import it.polimi.ingsw.common.backend.model.Player;

/**
 * This class represents a resource type that, when given to a player, increments its faith points.
 */
public class IncrementFaithPointsResType extends ResourceType {
    /**
     * Class constructor.
     *
     * @param name      the name of the resource type
     * @param storable  <code>true</code> if the resources of this type can be stored in a resource container;
     *                  <code>false</code> otherwise.
     * @param ansiColor the resource's color in ANSI format
     */
    public IncrementFaithPointsResType(String name, boolean storable, String ansiColor) {
        super(name, ansiColor, storable);
    }

    @Override
    public boolean isGiveableToPlayer() {
        return true;
    }

    @Override
    public void giveToPlayer(Game game, Player player, int quantity) {
        player.incrementFaithPoints(game, quantity);
    }
}
