package it.polimi.ingsw.common.events.mvevents;

import it.polimi.ingsw.common.View;

/** Victory points update. */
public class UpdateVictoryPoints extends ViewEvent {
    /** The player whose victory points increased. */
    private final String player;

    /** The updated points. */
    private final int victoryPoints;

    /**
     * Class constructor.
     *
     * @param view
     * @param player        the nickname of the player whose victory points increased
     * @param victoryPoints the updated victory points
     */
    public UpdateVictoryPoints(View view, String player, int victoryPoints) {
        super(view);
        this.player = player;
        this.victoryPoints = victoryPoints;
    }

    public UpdateVictoryPoints(String player, int victoryPoints) {
        this(null, player, victoryPoints);
    }

    /**
     * @return the nickname of the player whose victory points increased
     */
    public String getPlayer() {
        return player;
    }

    /**
     * @return the updated victory points
     */
    public int getVictoryPoints() {
        return victoryPoints;
    }
}
