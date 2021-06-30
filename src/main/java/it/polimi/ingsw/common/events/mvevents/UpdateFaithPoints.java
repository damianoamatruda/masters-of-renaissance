package it.polimi.ingsw.common.events.mvevents;

/** Faith track state update. */
public class UpdateFaithPoints implements MVEvent {
    /** The player whose faith points increased. */
    private final String player;

    /** <code>true</code> if the new position refers to the black cross marker; <code>false</code> otherwise. */
    private final boolean isBlackCross;

    /** The new marker position. */
    private final int faithPoints;

    /**
     * Class constructor.
     *
     * @param player       the nickname of the player whose faith points increased
     * @param faithPoints  <code>true</code> if the new position refers to the black cross marker; <code>false</code>
     *                     otherwise.
     * @param isBlackCross the new marker position
     */
    public UpdateFaithPoints(String player, int faithPoints, boolean isBlackCross) {
        this.player = player;
        this.faithPoints = faithPoints;
        this.isBlackCross = isBlackCross;
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
