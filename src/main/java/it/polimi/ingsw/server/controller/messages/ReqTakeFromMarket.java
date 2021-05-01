package it.polimi.ingsw.server.controller.messages;

import it.polimi.ingsw.server.NicknameRegister;
import it.polimi.ingsw.server.controller.Controller;

import java.io.PrintWriter;

public class ReqTakeFromMarket implements Message {
    @Override
    public void handle(Controller controller, NicknameRegister nicknameRegister, String nickname, PrintWriter out) {
        controller.handle(this, nicknameRegister, nickname, out);
    }
}
