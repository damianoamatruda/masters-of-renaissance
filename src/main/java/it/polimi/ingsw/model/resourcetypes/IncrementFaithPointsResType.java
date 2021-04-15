package it.polimi.ingsw.model.resourcetypes;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;

/**
 * ResourceType that, when given to a player, increments their faith points.
 */
public class IncrementFaithPointsResType extends ResourceType {
    /**
     * Class constructor.
     *
     * @param name     the name of the resource being created.
     * @param storable whether the resource can be stored on a <code>Shelf</code>
     */
    public IncrementFaithPointsResType(String name, boolean storable) {
        super(name, storable);
    }

    @Override
    public void giveToPlayer(Game game, Player player) {
        player.incrementFaithPoints(game);
    }
}
