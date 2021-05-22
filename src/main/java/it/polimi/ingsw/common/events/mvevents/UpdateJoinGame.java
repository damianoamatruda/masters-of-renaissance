package it.polimi.ingsw.common.events.mvevents;

import it.polimi.ingsw.common.View;

/** Server response to a new game request. */
public class UpdateJoinGame extends ViewEvent {
    /** The number of players the current game is set to. */
    private final int playersCount;

    /**
     * Class constructor.
     *
     * @param playersCount the number of players the current game is set to
     */
    public UpdateJoinGame(View view, int playersCount) {
        super(view);
        this.playersCount = playersCount;
    }

    /**
     * @return the current game's players' count
     */
    public int getPlayersCount() {
        return playersCount;
    }
}
