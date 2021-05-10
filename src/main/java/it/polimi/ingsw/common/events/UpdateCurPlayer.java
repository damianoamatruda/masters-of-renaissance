package it.polimi.ingsw.common.events;

import it.polimi.ingsw.common.View;

/** Current player state update. */
public class UpdateCurPlayer implements MVEvent {
    /** The nickname of the new current player. */
    private final String nickname;

    /**
     * Class constructor.
     * 
     * @param nickname the nickname of the new current player
     */
    public UpdateCurPlayer(String nickname) {
        this.nickname = nickname;
    }

    /**
     * @return the nickname of the new current player
     */
    public String getNickname() {
        return nickname;
    }

    @Override
    public void handle(View view) {
        // TODO Auto-generated method stub
        
    }
}
