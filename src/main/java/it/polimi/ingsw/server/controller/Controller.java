package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.controller.messages.Message;
import it.polimi.ingsw.server.model.Lobby;

import java.net.Socket;

public class Controller {
    private final Lobby model;

    public Controller(Lobby model) {
        this.model = model;
    }

    public void joinLobby(Socket client) {
        model.joinLobby(client);
    }

    public void handle(Message m, Socket c){
        m.handle(model.getJoinedGame(c), model.getPlayer(c));
    }

    // public void handle(MessageFirstType m, Socket c) {
    // }

    // public void handle(MessageSecondType m, Socket c) {
    // }
}
