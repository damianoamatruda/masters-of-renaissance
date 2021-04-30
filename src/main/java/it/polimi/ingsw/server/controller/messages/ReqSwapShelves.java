package it.polimi.ingsw.server.controller.messages;

import it.polimi.ingsw.server.NicknameRegister;
import it.polimi.ingsw.server.controller.Controller;

import java.io.PrintWriter;
import java.util.List;

public class ReqSwapShelves implements Message {
    private List<Integer> shelves;

    @Override
    public void handle(Controller controller, NicknameRegister nicknameRegister, String nickname, PrintWriter out) {
        controller.handle(this, nicknameRegister, nickname, out);
    }
}
