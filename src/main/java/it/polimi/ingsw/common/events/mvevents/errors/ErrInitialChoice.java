package it.polimi.ingsw.common.events.mvevents.errors;

import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.events.mvevents.ViewEvent;

/** Error event relative to a client request for a setup choice. */
public class ErrInitialChoice extends ViewEvent {
    private final boolean isLeadersChoice;
    /** The count of missing leader cards in the invalid choice. */
    private final int missingLeadersCount;

    /**
     * @param view
     * @param isLeadersChoice     whether the error refers to a leaderchoice or a resourcechoice
     * @param missingLeadersCount the count of missing leader cards in the invalid choice
     */
    public ErrInitialChoice(View view, boolean isLeadersChoice, int missingLeadersCount) {
        super(view);
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
