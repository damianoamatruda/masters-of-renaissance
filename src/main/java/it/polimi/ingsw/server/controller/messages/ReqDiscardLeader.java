package it.polimi.ingsw.server.controller.messages;

import it.polimi.ingsw.server.NicknameRegister;
import it.polimi.ingsw.server.controller.Controller;

import java.io.PrintWriter;

public class ReqDiscardLeader implements Message {
    private int leader;

    @Override
    public void handle(Controller controller, NicknameRegister nicknameRegister, String nickname, PrintWriter out) {
        controller.handle(this, nicknameRegister, nickname, out);
    }

    public int getLeader() {
        return leader;
    }
}
