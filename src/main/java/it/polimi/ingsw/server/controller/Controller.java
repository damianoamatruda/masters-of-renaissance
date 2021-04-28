package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.controller.messages.Message;
import it.polimi.ingsw.server.model.Lobby;
import it.polimi.ingsw.server.model.Player;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Controller {
    private final Lobby model;
    private final Map<Socket, Player> players;

    public Controller(Lobby model) {
        this.model = model;
        this.players = new HashMap<>();
    }

    public void assignPlayer(Socket client, Player player) {
        players.put(client, player);
    }

    public void joinLobby(Socket client) {
        model.joinLobby(players.get(client));
    }

    public void handle(Message message, Socket client) {
        Player player = players.get(client);
        message.handle(model.getJoinedGame(player).orElseThrow(), player);
    }

    // public void handle(MessageFirstType m, Socket c) {
    // }

    // public void handle(MessageSecondType m, Socket c) {
    // }
}
