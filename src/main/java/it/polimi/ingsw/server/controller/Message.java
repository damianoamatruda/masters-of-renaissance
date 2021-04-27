package it.polimi.ingsw.server.controller;

@FunctionalInterface
public interface Message {
    void handle(Controller controller);
}
