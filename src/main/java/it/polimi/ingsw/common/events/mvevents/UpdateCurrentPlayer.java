package it.polimi.ingsw.common.events.mvevents;

import it.polimi.ingsw.common.View;

/** Current player state update. */
public class UpdateCurrentPlayer extends ViewEvent {
    /** The nickname of the new current player. */
    private final String player;

    /**
     * Class constructor.
     *
     * @param view
     * @param player the nickname of the new current player
     */
    public UpdateCurrentPlayer(View view, String player) {
        super(view);
        this.player = player;
    }

    public UpdateCurrentPlayer(String player) {
        this(null, player);
    }

    /**
     * @return the nickname of the new current player
     */
    public String getPlayer() {
        return player;
    }
}
