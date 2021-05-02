package it.polimi.ingsw.server;

import it.polimi.ingsw.server.view.View;

@FunctionalInterface
public interface NicknameRegister {
    void registerNickname(View view, String nickname);
}
