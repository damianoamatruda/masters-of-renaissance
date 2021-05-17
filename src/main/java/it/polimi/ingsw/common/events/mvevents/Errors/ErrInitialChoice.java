package it.polimi.ingsw.common.events.mvevents.Errors;

import it.polimi.ingsw.common.events.mvevents.MVEvent;

/** Error event relative to a client request for a setup choice. */
public class ErrInitialChoice implements MVEvent {
    private final boolean isLeadersChoice;
    /** The count of missing leader cards in the invalid choice. */
    private final int missingLeadersCount;
    
    /**
     * @param isLeadersChoice     whether the error refers to a leaderchoice or a resourcechoice
     * @param missingLeadersCount the count of missing leader cards in the invalid choice
     */
    public ErrInitialChoice(boolean isLeadersChoice, int missingLeadersCount) {
        this.isLeadersChoice = isLeadersChoice;
        this.missingLeadersCount = missingLeadersCount;
    }
    /**
     * @return the isLeadersChoice
     */
    public boolean isLeadersChoice() {
        return isLeadersChoice;
    }
    /**
     * @return the missingLeadersCount
     */
    public int getMissingLeadersCount() {
        return missingLeadersCount;
    }
}
