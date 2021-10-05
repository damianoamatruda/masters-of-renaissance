package it.polimi.ingsw.common.backend.model;

/**
 * Exception thrown when a player tries to obtain bonus resources but has already done it.
 */
public class CannotChooseException extends RuntimeException {
    private final int missingLeadersCount;

    /**
     * Class constructor.
     *
     * @param missingLeadersCount the count of missing leader cards. Can be zero in case of a resourcechoice.
     */
    public CannotChooseException(int missingLeadersCount) {
        this.missingLeadersCount = missingLeadersCount;
    }

    /**
     * @return the missingLeadersCount
     */
    public int getMissingLeadersCount() {
        return missingLeadersCount;
    }
}
