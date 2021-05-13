package it.polimi.ingsw.common.events.mvevents;

import it.polimi.ingsw.common.View;

/** Server response to a new game request. */
public class UpdateFreeSeats implements MVEvent {
    /** The number of players the current game is set to. */
    private final int playersCount;

    /** The number of players that still need to join before the game can start. */
    private final int freeSeats;

    /**
     * Class constructor.
     *
     * @param playersCount the number of players the current game is set to
     * @param freeSeats    the number of players that still need to join before the game can start
     */
    public UpdateFreeSeats(int playersCount, int freeSeats) {
        this.playersCount = playersCount;
        this.freeSeats = freeSeats;
    }

    @Override
    public void handle(View view) {
        view.update(this);
    }

    /**
     * @return the current game's players' count
     */
    public int getPlayersCount() {
        return playersCount;
    }

    /**
     * @return the number of players that still need to join before the game can start
     */
    public int getFreeSeats() {
        return freeSeats;
    }
}
