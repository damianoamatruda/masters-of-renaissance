package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.controller.messages.*;
import it.polimi.ingsw.server.model.Lobby;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Controller {
    private final Lobby model;
    private final Map<Socket, String> nicknames;

    public Controller(Lobby model) {
        this.model = model;
        this.nicknames = new HashMap<>();
    }

    public void handle(ReqNickname message, Socket client) {
        nicknames.put(client, message.getNickname());
        // model.joinLobby(nicknames.get(client));
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

    public void handle(ReqPlayersCount message, Socket client) {

    }

    public void handle(ReqResourceChoice message, Socket client) {

    }

    public void handle(ReqSwapShelves message, Socket client) {

    }

    public void handle(ReqTurnEnd message, Socket client) {

    }
}
