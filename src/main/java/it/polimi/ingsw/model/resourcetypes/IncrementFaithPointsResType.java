package it.polimi.ingsw.model.resourcetypes;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;

public class IncrementFaithPointsResType extends ResourceType {
    public IncrementFaithPointsResType(String name, boolean storable) {
        super(name, storable);
    }

    @Override
    public void giveToPlayer(Game game, Player player) {
        player.incrementFaithPoints(game);
    }
}
