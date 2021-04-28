package it.polimi.ingsw.server.controller.messages;

import it.polimi.ingsw.server.controller.Controller;

import java.net.Socket;

public class ReqActivateLeader implements Message {
    private int leaderId;

    @Override
    public void handle(Controller controller, Socket client) {

    }
}
