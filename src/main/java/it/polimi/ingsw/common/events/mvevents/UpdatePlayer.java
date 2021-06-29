package it.polimi.ingsw.common.events.mvevents;

import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.reducedmodel.ReducedPlayerSetup;

import java.util.List;

public class UpdatePlayer extends ViewEvent {
    private final String player;
    private final int baseProduction;
    private final List<Integer> warehouseShelves;
    private final int strongbox;
    private final ReducedPlayerSetup playerSetup;
    private int faithPoints;

    /**
     * Class constructor.
     */
    public UpdatePlayer(View view, String player, int baseProduction, List<Integer> warehouseShelves, int strongbox,
                        ReducedPlayerSetup playerSetup, int faithPoints) {
        super(view);
        this.player = player;
        this.baseProduction = baseProduction;
        this.warehouseShelves = warehouseShelves;
        this.strongbox = strongbox;
        this.playerSetup = playerSetup;
        this.faithPoints = faithPoints;
    }

    /**
     * @return the nickname of the player that has chosen the leader cards
     */
    public String getPlayer() {
        return player;
    }

    /**
     * @return the ID of the base production
     */
    public int getBaseProduction() {
        return baseProduction;
    }

    /**
     * @return the player's shelves' IDs
     */
    public List<Integer> getWarehouseShelves() {
        return warehouseShelves;
    }

    /**
     * @return the player's strongbox ID
     */
    public int getStrongbox() {
        return strongbox;
    }

    /**
     * @return the player's setup
     */
    public ReducedPlayerSetup getPlayerSetup() {
        return playerSetup;
    }

    public int getFaithPoints() {
        return faithPoints;
    }
}
