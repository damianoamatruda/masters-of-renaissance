package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.controller.messages.*;
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

    public void handle(ReqActivateLeader message, Socket client) {

    }

    public void handle(ReqActivateProduction message, Socket client) {

    }

    public void handle(ReqBuyDevCard message, Socket client) {

    }

    public void handle(ReqDiscardLeader message, Socket client) {
        System.out.println("Discarding leader of index " + message.getLeaderId() + "...");
    }

    public void handle(ReqGetMarket message, Socket client) {

    }

    public void handle(ReqLeaderChoice message, Socket client) {

    }

    public void handle(ReqNickname message, Socket client) {

    }

    public void handle(ReqPlayersCount message, Socket client) {

    }

    public void handle(ReqResourceChoice message, Socket client) {

    }

    public void handle(ReqSwapShelves message, Socket client) {

    }

    public void handle(ReqTurnEnd message, Socket client) {

    }
}
