package it.polimi.ingsw.common.events;

import it.polimi.ingsw.common.View;

public class UpdateCurPlayer implements MVEvent {
    public final String nickname;

    public UpdateCurPlayer(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public void handle(View view) {
        // TODO Auto-generated method stub
        
    }
}
