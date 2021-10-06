package it.polimi.ingsw.common.events.mvevents;

import it.polimi.ingsw.common.View;

/**
 * Server response to a new game request.
 */
public class UpdateBookedSeats extends ViewEvent {
    /** The number of players that are booked to join before the game is prepared. */
    private final int bookedSeats;

    /** The nickname of the player that can prepare a new game. */
    private final String canPrepareNewGame;

    /**
     * Class constructor.
     *
     * @param bookedSeats       the number of players that are booked to join before the game is prepared
     * @param canPrepareNewGame the nickname of the player that can prepare a new game
     */
    public UpdateBookedSeats(View view, int bookedSeats, String canPrepareNewGame) {
        super(view);
        this.bookedSeats = bookedSeats;
        this.canPrepareNewGame = canPrepareNewGame;
    }

    /**
     * @return the number of players that are booked to join before the game is prepared
     */
    public int getBookedSeats() {
        return bookedSeats;
    }

    /**
     * @return the nickname of the player that can prepare a new game
     */
    public String canPrepareNewGame() {
        return canPrepareNewGame;
    }
}
