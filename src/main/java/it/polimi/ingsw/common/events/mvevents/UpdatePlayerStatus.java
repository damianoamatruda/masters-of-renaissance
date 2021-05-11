package it.polimi.ingsw.common.events.mvevents;

import it.polimi.ingsw.common.View;

/** Player connection status update. */
public class UpdatePlayerStatus implements MVEvent {
    /** The player the new status refers to. */
    private final String nickname;
    /** Whether the player is now active. */
    private final boolean isActive;

    /**
     * Class constructor.
     * 
     * @param nickname the player the new status refers to
     * @param isActive whether the player is now active
     */
    public UpdatePlayerStatus(String nickname, boolean isActive) {
        this.nickname = nickname;
        this.isActive = isActive;
    }

    /**
     * @return whether the player is now active
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

    @Override
    public void handle(View view) {
        // TODO Auto-generated method stub
        
    }
}
