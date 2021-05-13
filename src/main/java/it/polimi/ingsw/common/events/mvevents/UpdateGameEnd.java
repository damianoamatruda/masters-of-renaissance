package it.polimi.ingsw.common.events.mvevents;

import it.polimi.ingsw.common.View;

/** Winner player and victory points state update. */
public class UpdateGameEnd implements MVEvent {
    /**
     * The nickname of the player who won the game. A null value signifies that the black cross marker is the winner.
     */
    private final String winner;

    /**
     * Class constructor.
     *
     * @param winner the nickname of the player who won the game A null value signifies that the black cross marker is
     *               the winner.
     */
    public UpdateGameEnd(String winner) {
        this.winner = winner;
    }

    @Override
    public void handle(View view) {
        view.update(this);
    }

    /**
     * @return the nickname of the player who won the game. A null value signifies that the black cross marker is the
     * winner.
     */
    public String getWinner() {
        return winner;
    }
}
