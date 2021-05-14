package it.polimi.ingsw.common.events.mvevents;

import it.polimi.ingsw.common.View;

/** Server confirmation to the leader choice request during player setup. */
public class UpdateChooseLeaders implements MVEvent {
    /** The nickname of the player that has chosen the leader cards. */
    private final String player;

    /**
     * Class constructor.
     *
     * @param player the nickname of the player that has chosen the leader cards
     */
    public UpdateChooseLeaders(String player) {
        this.player = player;
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
}
