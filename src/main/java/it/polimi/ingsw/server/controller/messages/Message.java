package it.polimi.ingsw.server.controller.messages;

import it.polimi.ingsw.server.NicknameRegister;
import it.polimi.ingsw.server.controller.Controller;

import java.io.PrintWriter;

@FunctionalInterface
public interface Message {
    void handle(Controller controller, NicknameRegister nicknameRegister, String nickname, PrintWriter out);
}
