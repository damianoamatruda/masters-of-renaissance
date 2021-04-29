package it.polimi.ingsw.server.controller.messages;

import it.polimi.ingsw.server.controller.Controller;

import java.io.PrintWriter;
import java.net.Socket;

public class ReqNickname implements Message {
    private String nickname;

    @Override
    public void handle(Controller controller, Socket client, PrintWriter out) {
        controller.handle(this, client, out);
    }

    public String getNickname() {
        return nickname;
    }
}
