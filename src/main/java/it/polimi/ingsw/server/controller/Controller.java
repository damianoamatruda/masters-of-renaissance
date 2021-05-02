package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.NicknameRegister;
import it.polimi.ingsw.server.model.Lobby;
import it.polimi.ingsw.server.model.leadercards.LeaderCard;
import it.polimi.ingsw.server.view.View;

public class Controller {
    private final NicknameRegister nicknameRegister;
    private final Lobby model;

    public Controller(NicknameRegister nicknameRegister, Lobby model) {
        this.nicknameRegister = nicknameRegister;
        this.model = model;
    }

    public void registerNickname(View view, String nickname) {
        nicknameRegister.registerNickname(view, nickname);
        System.out.println("Setting nickname \"" + nickname + "\"...");
        model.joinLobby(nickname);
    }

    public void activateLeader(View view, LeaderCard leader) {
        checkView(view);
        System.out.println("Activating leader...");
    }

    public void activateProduction(View view) {
        checkView(view);
        System.out.println("Activating production...");
    }

    public void buyDevCard(View view) {
        checkView(view);
        System.out.println("Buying development card...");
    }

    public void discardLeader(View view, LeaderCard leader) {
        checkView(view);
        System.out.println("Discarding leader...");
    }

    public void getMarket(View view) {
        checkView(view);
        System.out.println("Getting market...");
    }

    public void chooseLeaders(View view) {
        checkView(view);
        System.out.println("Choosing leaders...");
    }

    public void setPlayersCount(View view) {
        // TODO
        checkView(view);
        /*if (!model.isPlayerFirst(nickname))
            throw new RuntimeException("Command unavailable. You are not the first player who joined.");
        System.out.println("Setting count of players...");
        model.setCountToNewGame(message.getCount());*/
    }

    public void chooseResources(View view) {
        checkView(view);
        System.out.println("Choosing initial resources...");
    }

    public void swapShelves(View view) {
        checkView(view);
        System.out.println("Swapping shelves...");
    }

    public void takeFromMarket(View view) {
        checkView(view);
        System.out.println("Taking resources from market...");
    }

    public void endTurn(View view) {
        checkView(view);
        System.out.println("Ending turn...");
    }

    private void checkView(View view) {
        // TODO
        /*if (nickname == null)
            throw new RuntimeException("Client must first request a nickname.");*/
    }
}
