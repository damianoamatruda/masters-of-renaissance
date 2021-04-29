package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.controller.messages.*;
import it.polimi.ingsw.server.model.Lobby;

import java.io.PrintWriter;
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

    public void handle(ReqNickname message, Socket client, PrintWriter out) {
        if (nicknames.containsKey(client))
            throw new RuntimeException("Client already has nickname \"" + nicknames.get(client) + "\".");
        if (nicknames.containsValue(message.getNickname()))
            throw new RuntimeException("Nickname \"" + message.getNickname() + "\" already in use. Choose another one.");
        out.println("Setting nickname...");
        nicknames.put(client, message.getNickname());
        // model.joinLobby(nicknames.get(client));
    }

    public void handle(ReqActivateLeader message, Socket client, PrintWriter out) {
        checkClient(client);
        out.println("Activating leader...");
    }

    public void handle(ReqActivateProduction message, Socket client, PrintWriter out) {
        checkClient(client);
        out.println("Activating production...");
    }

    public void handle(ReqBuyDevCard message, Socket client, PrintWriter out) {
        checkClient(client);
        out.println("Buying development card...");
    }

    public void handle(ReqDiscardLeader message, Socket client, PrintWriter out) {
        checkClient(client);
        out.println("Discarding leader of index " + message.getLeaderId() + "...");
    }

    public void handle(ReqGetMarket message, Socket client, PrintWriter out) {
        checkClient(client);
        out.println("Getting market...");
    }

    public void handle(ReqLeaderChoice message, Socket client, PrintWriter out) {
        checkClient(client);
        out.println("Choosing leader...");
    }

    public void handle(ReqPlayersCount message, Socket client, PrintWriter out) {
        checkClient(client);
        out.println("Counting players...");
    }

    public void handle(ReqResourceChoice message, Socket client, PrintWriter out) {
        checkClient(client);
        out.println("Choosing initial resources...");
    }

    public void handle(ReqSwapShelves message, Socket client, PrintWriter out) {
        checkClient(client);
        out.println("Swapping shelves...");
    }

    public void handle(ReqTurnEnd message, Socket client, PrintWriter out) {
        checkClient(client);
        out.println("Ending turn...");
    }

    private void checkClient(Socket client) {
        if (!nicknames.containsKey(client))
            throw new RuntimeException("Client must first request a nickname.");
    }
}
