package it.polimi.ingsw.common.events.mvevents;

import it.polimi.ingsw.common.View;

/** Current player state update. */
public class UpdateCurrentPlayer implements MVEvent {
    /** The nickname of the new current player. */
    private final String nickname;

    /**
     * Class constructor.
     *
     * @param nickname the nickname of the new current player
     */
    public UpdateCurrentPlayer(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public void handle(View view) {
        view.update(this);
    }

    /**
     * @return the nickname of the new current player
     */
    public String getNickname() {
        return nickname;
    }
}
