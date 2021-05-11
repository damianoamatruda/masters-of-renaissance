package it.polimi.ingsw.common.events.mvevents;

import it.polimi.ingsw.common.View;

/** Faith track state update. */
public class UpdateFaithTrack implements MVEvent {
    /** The player whose faith points increased. */
    private final String nickname;

    /** <code>true</code> if the new position refers to the black cross marker; <code>false</code> otherwise. */
    private final boolean isBlackCross;

    /** The new marker position. */
    private final int position;

    /**
     * Class constructor.
     *
     * @param nickname     the nickname of the player whose faith points increased
     * @param newPos       <code>true</code> if the new position refers to the black cross marker; <code>false</code>
     *                     otherwise.
     * @param isBlackCross the new marker position
     */
    public UpdateFaithTrack(String nickname, int newPos, boolean isBlackCross) {
        this.nickname = nickname;
        this.position = newPos;
        this.isBlackCross = isBlackCross;
    }

    @Override
    public void handle(View view) {
        view.update(this);
    }

    /**
     * @return the nickname of the player whose faith points increased
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * @return the new marker position
     */
    public int getPosition() {
        return position;
    }

    /**
     * @return <code>true</code> if the new position refers to the black cross marker; <code>false</code> otherwise.
     */
    public boolean isBlackCross() {
        return isBlackCross;
    }
}
