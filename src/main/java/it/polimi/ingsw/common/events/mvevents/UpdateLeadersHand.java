package it.polimi.ingsw.common.events.mvevents;

import java.util.List;

/** Server confirmation to the leader choice request during player setup. */
public class UpdateLeadersHand implements MVEvent {
    /** The nickname of the player that has chosen the leader cards. */
    private final String player;

    /** The number of leader cards in the hand of the player. */
    private final List<Integer> leaders;

    /**
     * Class constructor.
     *
     * @param player  the nickname of the player that has chosen the leader cards
     * @param leaders the number of leader cards in the hand of the player
     */
    public UpdateLeadersHand(String player, List<Integer> leaders) {
        this.player = player;
        this.leaders = leaders;
    }

    /**
     * @return the nickname of the player that has chosen the leader cards
     */
    public String getPlayer() {
        return player;
    }

    /**
     * @return the player's leaders' IDs
     */
    public List<Integer> getLeaders() {
        return leaders;
    }
}
