package it.polimi.ingsw.server.controller;

import java.net.Socket;

@FunctionalInterface
public interface Message {
    void handle(Controller controller, Socket client);
}
