package it.polimi.ingsw.server.controller.messages;

import it.polimi.ingsw.server.controller.Controller;

import java.net.Socket;

public class ReqPlayersCount implements Message {
    private int count;

    @Override
    public void handle(Controller controller, Socket client) {

    }
}
