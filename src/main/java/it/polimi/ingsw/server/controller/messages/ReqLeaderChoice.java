package it.polimi.ingsw.server.controller.messages;

import it.polimi.ingsw.server.controller.Controller;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class ReqLeaderChoice implements Message {
    private List<Integer> leadersId;

    @Override
    public void handle(Controller controller, Socket client, PrintWriter out) {
        controller.handle(this, client, out);
    }
}
