package it.polimi.ingsw.common.events.vcevents;

/** Client request for a new game. */
public class ReqNewGame implements VCEvent {
    /** The number of players needed to start the game. */
    private final int playersCount;

    /**
     * Class constructor.
     *
     * @param playersCount the number of players needed to start the game
     */
    public ReqNewGame(int playersCount) {
        this.playersCount = playersCount;
    }

    /**
     * @return the number of players needed to start the game
     */
    public int getPlayersCount() {
        return playersCount;
    }
}
