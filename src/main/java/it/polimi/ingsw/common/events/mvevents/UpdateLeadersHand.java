package it.polimi.ingsw.common.events.mvevents;

import it.polimi.ingsw.common.View;

/** Server confirmation to the leader choice request during player setup. */
public class UpdateLeadersHand implements MVEvent {
    /** The nickname of the player that has chosen the leader cards. */
    private final String player;

    /** The number of leader cards in the hand of the player. */
    private final int leadersCount;

    /**
     * Class constructor.
     *
     * @param player       the nickname of the player that has chosen the leader cards
     * @param leadersCount the number of leader cards in the hand of the player
     */
    public UpdateLeadersHand(String player, int leadersCount) {
        this.player = player;
        this.leadersCount = leadersCount;
    }

    @Override
    public void handle(View view) {
        view.update(this);
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
    public int getLeadersCount() {
        return leadersCount;
    }
}