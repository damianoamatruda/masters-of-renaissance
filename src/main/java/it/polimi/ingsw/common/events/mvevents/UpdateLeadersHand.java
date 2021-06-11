package it.polimi.ingsw.common.events.mvevents;

import it.polimi.ingsw.common.View;

import java.util.List;

/** Server confirmation to the leader choice request during player setup. */
public class UpdateLeadersHand extends ViewEvent {
    /** The nickname of the player that has chosen the leader cards. */
    private final String player;

    /** The number of leader cards in the hand of the player. */
    private final List<Integer> leaders;

    /**
     * Class constructor.
     *
     * @param view
     * @param player  the nickname of the player that has chosen the leader cards
     * @param leaders the number of leader cards in the hand of the player
     */
    public UpdateLeadersHand(View view, String player, List<Integer> leaders) {
        super(view);
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
