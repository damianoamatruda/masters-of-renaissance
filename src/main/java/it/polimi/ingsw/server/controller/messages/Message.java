package it.polimi.ingsw.server.controller.messages;

import it.polimi.ingsw.server.controller.Controller;

import java.io.PrintWriter;
import java.net.Socket;

@FunctionalInterface
public interface Message {
    void handle(Controller controller, Socket client, PrintWriter out);
}
