package it.polimi.ingsw.server.model;

/**
 * Exception thrown when an active leader card is attempted to be discarded.
 */
public class ActiveLeaderDiscardException extends Exception {
    /**
     * Class constructor.
     *
     * @param leaderIndex the index of the leader card that is trying to be discarded while already active.
     */
    public ActiveLeaderDiscardException(int leaderIndex) {
        super(String.format("Cannot discard leader %d: leader card is active", leaderIndex));
    }
}
