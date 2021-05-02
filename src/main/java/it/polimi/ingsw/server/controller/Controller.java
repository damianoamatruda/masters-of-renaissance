package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.model.Lobby;
import it.polimi.ingsw.server.model.leadercards.LeaderCard;
import it.polimi.ingsw.server.view.View;

public class Controller {
    private final Lobby model;

    public Controller(Lobby model) {
        this.model = model;
    }

    public void quit(View view) {
        view.updateGoodbye();
    }

    public void registerNickname(View view, String nickname) {
        model.joinLobby(view, nickname);
    }

    public void activateLeader(View view, LeaderCard leader) {
        if (model.checkView(view))
            System.out.println("Activating leader...");
    }

    public void activateProduction(View view) {
        if (model.checkView(view))
            System.out.println("Activating production...");
    }

    public void buyDevCard(View view) {
        if (model.checkView(view))
            System.out.println("Buying development card...");
    }

    public void discardLeader(View view, LeaderCard leader) {
        if (model.checkView(view))
            System.out.println("Discarding leader...");
    }

    public void getMarket(View view) {
        if (model.checkView(view))
            System.out.println("Getting market...");
    }

    public void chooseLeaders(View view) {
        if (model.checkView(view))
            System.out.println("Choosing leaders...");
    }

    public void setPlayersCount(View view, int count) {
        if (model.checkView(view))
            model.setCountToNewGame(view, count);
    }

    public void chooseResources(View view) {
        if (model.checkView(view))
            System.out.println("Choosing initial resources...");
    }

    public void swapShelves(View view) {
        if (model.checkView(view))
            System.out.println("Swapping shelves...");
    }

    public void takeFromMarket(View view) {
        if (model.checkView(view))
            System.out.println("Taking resources from market...");
    }

    public void endTurn(View view) {
        if (model.checkView(view))
            System.out.println("Ending turn...");
    }
}
