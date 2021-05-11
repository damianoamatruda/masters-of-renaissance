package it.polimi.ingsw.common.events.mvevents;

import it.polimi.ingsw.common.View;

/** Server response to a new game request. */
public class UpdateFreeSeats implements MVEvent {
    /** The number of players the current game is set to. */
    private final int countToNewGame;

    /** The number of players that still need to join before the game can start. */
    private final int freeSeats;

    /**
     * Class constructor.
     *
     * @param countToNewGame the number of players the current game is set to
     * @param freeSeats      the number of players that still need to join before the game can start
     */
    public UpdateFreeSeats(int countToNewGame, int freeSeats) {
        this.countToNewGame = countToNewGame;
        this.freeSeats = freeSeats;
    }

    @Override
    public void handle(View view) {
        view.update(this);
    }

    /**
     * @return the current game's players' count
     */
    public int getCountToNewGame() {
        return countToNewGame;
    }

    /**
     * @return the number of players that still need to join before the game can start
     */
    public int getFreeSeats() {
        return freeSeats;
    }
}
