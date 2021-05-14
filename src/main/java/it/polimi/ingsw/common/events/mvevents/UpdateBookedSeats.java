package it.polimi.ingsw.common.events.mvevents;

import it.polimi.ingsw.common.View;

/** Server response to a new game request. */
public class UpdateBookedSeats implements MVEvent {
    /** The number of players that are booked to join before the game is prepared. */
    private final int bookedSeats;

    /**
     * Class constructor.
     *
     * @param bookedSeats the number of players that are booked to join before the game is prepared
     */
    public UpdateBookedSeats(int bookedSeats) {
        this.bookedSeats = bookedSeats;
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
}
