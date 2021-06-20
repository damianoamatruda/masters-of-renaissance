package it.polimi.ingsw.common.reducedmodel;

import it.polimi.ingsw.common.backend.model.Player;

import java.util.ArrayList;
import java.util.List;

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

    /** The list of players that earned a Pope's favor in this section. */
    private ArrayList<String> bonusGivenPlayers;

    /**
     * @param id                    the ID of the vatican section
     * @param faithPointsBeginning  the first tile
     * @param faithPointsEnd        the last (Vatican Report) tile
     * @param victoryPoints         the bonus victory points of the section
     * @param activated             the status of activation
     * @param bonusGivenPlayers     the players that earned the bonus
     */
    public ReducedVaticanSection(int id, int faithPointsBeginning, int faithPointsEnd, int victoryPoints, boolean activated, List<String> bonusGivenPlayers) {
        this.id = id;
        this.faithPointsBeginning = faithPointsBeginning;
        this.faithPointsEnd = faithPointsEnd;
        this.victoryPoints = victoryPoints;
        this.activated = activated;
        this.bonusGivenPlayers = new ArrayList<>(bonusGivenPlayers);
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
     * Activates the section.
     */
    public void setActive() {
        this.activated = true;
    }

    /**
     * Updates the players that earned the bonus.
     *
     * @param bonusGivenPlayers the players that earned the bonus
     */
    public void setBonusGivenPlayers(List<String> bonusGivenPlayers) {
        this.bonusGivenPlayers = new ArrayList<>(bonusGivenPlayers);
    }

    /**
     * Getter of the players that earned the bonus.
     *
     * @return the players that earned the bonus
     */
    public ArrayList<String> getBonusGivenPlayers() {
        return bonusGivenPlayers;
    }
}
