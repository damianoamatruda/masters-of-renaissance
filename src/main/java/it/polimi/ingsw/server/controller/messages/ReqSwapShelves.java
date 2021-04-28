package it.polimi.ingsw.server.controller.messages;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.gamecontext.GameContext;
import it.polimi.ingsw.server.model.gamecontext.IllegalActionException;
import it.polimi.ingsw.server.model.resourcecontainers.IllegalResourceTransferException;

import java.util.List;

public class ReqSwapShelves implements Message {
    private List<Integer> shelves;

    @Override
    public void handle(GameContext context, Player player) {
        int s1 = shelves.get(0);
        int s2 = shelves.get(1);
        try {
            context.swapShelves(player, player.getWarehouse().getShelves().get(s1), player.getWarehouse().getShelves().get(s2));
            // method chain is too long, maybe pass params as integers to swapShelves?
        } catch (IllegalActionException | IllegalResourceTransferException e) {
            e.printStackTrace();
        }
    }
}
