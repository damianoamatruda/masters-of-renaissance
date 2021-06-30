package it.polimi.ingsw.common.events.mvevents;

import it.polimi.ingsw.common.View;

/** Faith track state update. */
public class UpdateFaithPoints extends ViewEvent {
    /** The player whose faith points increased. */
    private final String player;

    /** <code>true</code> if the new position refers to the black cross marker; <code>false</code> otherwise. */
    private final boolean isBlackCross;

    /** The new marker position. */
    private final int faithPoints;

    /**
     * Class constructor.
     *
     * @param view
     * @param player       the nickname of the player whose faith points increased
     * @param faithPoints  <code>true</code> if the new position refers to the black cross marker; <code>false</code>
     *                     otherwise.
     * @param isBlackCross the new marker position
     */
    public UpdateFaithPoints(View view, String player, int faithPoints, boolean isBlackCross) {
        super(view);
        this.player = player;
        this.faithPoints = faithPoints;
        this.isBlackCross = isBlackCross;
    }

    public UpdateFaithPoints(String player, int faithPoints, boolean isBlackCross) {
        this(null, player, faithPoints, isBlackCross);
    }

    /**
     * @return the nickname of the player whose faith points increased
     */
    public String getPlayer() {
        return player;
    }

    /**
     * @return the new marker position
     */
    public int getFaithPoints() {
        return faithPoints;
    }

    /**
     * @return <code>true</code> if the new position refers to the black cross marker; <code>false</code> otherwise.
     */
    public boolean isBlackCross() {
        return isBlackCross;
    }
}
