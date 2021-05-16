package it.polimi.ingsw.common.events.mvevents;

/** Server response to a new game request. */
public class UpdateJoinGame implements MVEvent {
    /** The number of players the current game is set to. */
    private final int playersCount;

    /**
     * Class constructor.
     *
     * @param playersCount the number of players the current game is set to
     */
    public UpdateJoinGame(int playersCount) {
        this.playersCount = playersCount;
    }

    /**
     * @return the current game's players' count
     */
    public int getPlayersCount() {
        return playersCount;
    }
}
