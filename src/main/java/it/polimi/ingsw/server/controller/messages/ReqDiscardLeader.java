package it.polimi.ingsw.server.controller.messages;

import it.polimi.ingsw.server.controller.Controller;

import java.io.PrintWriter;
import java.net.Socket;

public class ReqDiscardLeader implements Message {
    private int leaderId;

    @Override
    public void handle(Controller controller, Socket client, PrintWriter out) {
        controller.handle(this, client, out);
    }

    public int getLeaderId() {
        return leaderId;
    }
}
