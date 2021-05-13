package it.polimi.ingsw.common.events.mvevents;

import it.polimi.ingsw.common.View;

/** Victory points update. */
public class UpdateVictoryPoints implements MVEvent {
    /** The player whose victory points increased. */
    private final String player;

    /** The new marker position. */
    private final int victoryPoints;

    /**
     * Class constructor.
     *
     * @param player        the nickname of the player whose victory points increased
     * @param victoryPoints the updated victory points
     */
    public UpdateVictoryPoints(String player, int victoryPoints) {
        this.player = player;
        this.victoryPoints = victoryPoints;
    }

    @Override
    public void handle(View view) {
        view.update(this);
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
