package it.polimi.ingsw.common.events;

import it.polimi.ingsw.common.View;

public class UpdatePlayerConnectionStatus implements MVEvent {
    private final String nickname;
    private final boolean isActive;

    public UpdatePlayerConnectionStatus(String nickname, boolean isActive) {
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
     * @return the nickname of the player who disconnected
     */
    public String getNickname() {
        return nickname;
    }

    @Override
    public void handle(View view) {
        // TODO Auto-generated method stub
        
    }
}
