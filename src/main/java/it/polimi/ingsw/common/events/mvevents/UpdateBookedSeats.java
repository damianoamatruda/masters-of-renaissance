package it.polimi.ingsw.common.events.mvevents;

import it.polimi.ingsw.common.View;

/** Server response to a new game request. */
public class UpdateBookedSeats implements MVEvent {
    /** The number of players that are booked to join before the game is prepared. */
    private final int bookedSeats;

    /** <code>true</code> if the player can prepare a new game; <code>false</code> otherwise. */
    private final boolean canPrepareNewGame;

    /**
     * Class constructor.
     *
     * @param bookedSeats       the number of players that are booked to join before the game is prepared
     * @param canPrepareNewGame <code>true</code> if the player can prepare a new game; <code>false</code> otherwise.
     */
    public UpdateBookedSeats(int bookedSeats, boolean canPrepareNewGame) {
        this.bookedSeats = bookedSeats;
        this.canPrepareNewGame = canPrepareNewGame;
    }

    @Override
    public void handle(View view) {
        view.update(this);
    }

    /**
     * @return the number of players that are booked to join before the game is prepared
     */
    public int getBookedSeats() {
        return bookedSeats;
    }

    /**
     * @return <code>true</code> if the player can prepare a new game; <code>false</code> otherwise.
     */
    public boolean canPrepareNewGame() {
        return canPrepareNewGame;
    }
}
