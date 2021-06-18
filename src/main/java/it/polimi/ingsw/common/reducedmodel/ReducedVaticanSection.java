package it.polimi.ingsw.common.reducedmodel;

public class ReducedVaticanSection {
    private final int id;

    /** The first tile of the Vatican Section, which needs to be reached in order to earn bonus points. */
    private final int faithPointsBeginning;

    /** The last tile of the Vatican Section, which needs to be reached in order to activate a Vatican report. */
    private final int faithPointsEnd;

    /** The corresponding amount of bonus points that will be rewarded to the players after the Report is over. */
    private final int victoryPoints;

    /** <code>true</code> if the Vatican report is already over; <code>false</code> otherwise. */
    private boolean activated;

    /**
     * @param id                    the ID of the vatican section
     * @param faithPointsBeginning  the first tile
     * @param faithPointsEnd        the last (Vatican Report) tile
     * @param victoryPoints         the bonus victory points of the section
     */
    public ReducedVaticanSection(int id, int faithPointsBeginning, int faithPointsEnd, int victoryPoints) {
        this.id = id;
        this.faithPointsBeginning = faithPointsBeginning;
        this.faithPointsEnd = faithPointsEnd;
        this.victoryPoints = victoryPoints;
        this.activated = false;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @return the faithPointsBeginning
     */
    public int getFaithPointsBeginning() {
        return faithPointsBeginning;
    }

    /**
     * @return the faithPointsEnd
     */
    public int getFaithPointsEnd() {
        return faithPointsEnd;
    }

    /**
     * @return the victoryPoints
     */
    public int getVictoryPoints() {
        return victoryPoints;
    }

    /**
     * @return the activated
     */
    public boolean isActivated() {
        return activated;
    }

    /**
     * Activates the section
     */
    public void setActive() {
        this.activated = true;
    }
}
