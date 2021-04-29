package it.polimi.ingsw.server.controller.messages;

import it.polimi.ingsw.server.controller.Controller;

import java.net.Socket;

public class ReqResourceChoice implements Message {
    @Override
    public void handle(Controller controller, Socket client) {
        controller.handle(this, client);
    }
}
