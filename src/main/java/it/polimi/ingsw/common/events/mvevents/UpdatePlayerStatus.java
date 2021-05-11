package it.polimi.ingsw.common.events.mvevents;

import it.polimi.ingsw.common.View;

/** Player connection status update. */
public class UpdatePlayerStatus implements MVEvent {
    /** The player the new status refers to. */
    private final String nickname;

    /** <code>true</code> if the player is now active; <code>false</code> otherwise. */
    private final boolean isActive;

    /**
     * Class constructor.
     *
     * @param nickname the player the new status refers to
     * @param isActive <code>true</code> if the player is now active; <code>false</code> otherwise.
     */
    public UpdatePlayerStatus(String nickname, boolean isActive) {
        this.nickname = nickname;
        this.isActive = isActive;
    }

    @Override
    public void handle(View view) {
        view.update(this);
    }

    /**
     * @return <code>true</code> if the player is now active; <code>false</code> otherwise.
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * @return the nickname of the player the status of whom changed
     */
    public String getNickname() {
        return nickname;
    }
}
