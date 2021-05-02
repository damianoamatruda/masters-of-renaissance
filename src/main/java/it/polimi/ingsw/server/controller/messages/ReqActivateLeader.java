package it.polimi.ingsw.server.controller.messages;

import it.polimi.ingsw.server.NicknameRegister;
import it.polimi.ingsw.server.controller.Controller;

import java.io.PrintWriter;

public class ReqActivateLeader implements Message {
    private int leaderId;

    @Override
    public void handle(Controller controller, NicknameRegister nicknameRegister, String nickname, PrintWriter out) {
        controller.handle(this, nicknameRegister, nickname, out);
    }
}
