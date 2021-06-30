package it.polimi.ingsw.common.events.mvevents;

/** Server confirmation to the leader choice request during player setup. */
public class UpdateLeadersHandCount implements MVEvent {
    /** The nickname of the player that has chosen the leader cards. */
    private final String player;

    /** The number of leader cards in the hand of the player. */
    private final int leadersHandCount;

    /**
     * Class constructor.
     *
     * @param player           the nickname of the player that has chosen the leader cards
     * @param leadersHandCount the number of leader cards in the hand of the player
     */
    public UpdateLeadersHandCount(String player, int leadersHandCount) {
        this.player = player;
        this.leadersHandCount = leadersHandCount;
    }

    /**
     * @return the nickname of the player that has chosen the leader cards
     */
    public String getPlayer() {
        return player;
    }

    /**
     * @return the number of leader cards in the hand of the player
     */
    public int getLeadersHandCount() {
        return leadersHandCount;
    }
}
