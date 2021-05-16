package it.polimi.ingsw.common.events.mvevents;

/** Server response to a new game request. */
public class UpdateBookedSeats implements MVEvent {
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
    public UpdateBookedSeats(int bookedSeats, String canPrepareNewGame) {
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
