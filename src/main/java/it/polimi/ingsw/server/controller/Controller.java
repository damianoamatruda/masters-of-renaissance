package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.NicknameRegister;
import it.polimi.ingsw.server.controller.messages.*;
import it.polimi.ingsw.server.model.Lobby;

import java.io.PrintWriter;

public class Controller {
    private final Lobby model;

    public Controller(Lobby model) {
        this.model = model;
    }

    public void handle(ReqNickname message, NicknameRegister nicknameRegister, String nickname, PrintWriter out) {
        nicknameRegister.registerNickname(message.getNickname());
        out.println("Setting nickname \"" + message.getNickname() + "\"...");
        model.joinLobby(message.getNickname());
    }

    public void handle(ReqActivateLeader message, NicknameRegister nicknameRegister, String nickname, PrintWriter out) {
        checkClient(nickname);
        out.println("Activating leader...");
    }

    public void handle(ReqActivateProduction message, NicknameRegister nicknameRegister, String nickname, PrintWriter out) {
        checkClient(nickname);
        out.println("Activating production...");
    }

    public void handle(ReqBuyDevCard message, NicknameRegister nicknameRegister, String nickname, PrintWriter out) {
        checkClient(nickname);
        out.println("Buying development card...");
    }

    public void handle(ReqDiscardLeader message, NicknameRegister nicknameRegister, String nickname, PrintWriter out) {
        checkClient(nickname);
        out.println("Discarding leader of index " + message.getLeaderId() + "...");
    }

    public void handle(ReqGetMarket message, NicknameRegister nicknameRegister, String nickname, PrintWriter out) {
        checkClient(nickname);
        out.println("Getting market...");
    }

    public void handle(ReqChooseLeaders message, NicknameRegister nicknameRegister, String nickname, PrintWriter out) {
        checkClient(nickname);
        out.println("Choosing leader...");
    }

    public void handle(ReqPlayersCount message, NicknameRegister nicknameRegister, String nickname, PrintWriter out) {
        checkClient(nickname);
        if (!model.isPlayerFirst(nickname))
            throw new RuntimeException("Command unavailable. You are not the first player who joined.");
        out.println("Counting players...");
        model.setCountToNewGame(message.getCount());
    }

    public void handle(ReqResourcesChoice message, NicknameRegister nicknameRegister, String nickname, PrintWriter out) {
        checkClient(nickname);
        out.println("Choosing initial resources...");
    }

    public void handle(ReqSwapShelves message, NicknameRegister nicknameRegister, String nickname, PrintWriter out) {
        checkClient(nickname);
        out.println("Swapping shelves...");
    }

    public void handle(ReqTakeFromMarket message, NicknameRegister nicknameRegister, String nickname, PrintWriter out) {
        checkClient(nickname);
        out.println("Taking resources from market...");
    }

    public void handle(ReqTurnEnd message, NicknameRegister nicknameRegister, String nickname, PrintWriter out) {
        checkClient(nickname);
        out.println("Ending turn...");
    }

    private void checkClient(String nickname) {
        if (nickname == null)
            throw new RuntimeException("Client must first request a nickname.");
    }
}
