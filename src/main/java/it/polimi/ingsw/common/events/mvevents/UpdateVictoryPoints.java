package it.polimi.ingsw.common.events.mvevents;

import it.polimi.ingsw.common.View;

/** Victory points update. */
public class UpdateVictoryPoints implements MVEvent {
    /** The player whose victory points increased. */
    private final String nickname;

    /** The new marker position. */
    private final int points;

    /**
     * Class constructor.
     *
     * @param nickname the nickname of the player whose victory points increased
     * @param newPts   the updated victory points
     */
    public UpdateVictoryPoints(String nickname, int newPts) {
        this.nickname = nickname;
        this.points = newPts;
    }

    @Override
    public void handle(View view) {
        view.update(this);
    }

    /**
     * @return the nickname of the player whose victory points increased
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * @return the updated victory points
     */
    public int getPoints() {
        return points;
    }
}
