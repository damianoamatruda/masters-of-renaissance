package it.polimi.ingsw.server;

import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.events.*;
import it.polimi.ingsw.server.model.Lobby;

public class Controller {
    private final Lobby model;

    public Controller(Lobby model) {
        this.model = model;
    }

    public void update(View view, ReqQuit event) {
        model.exit(view);
    }

    public void update(View view, ReqNickname event) {
        model.joinLobby(view, event.getNickname());
    }

    public void update(View view, ReqPlayersCount event) {
        model.setCountToNewGame(view, event.getCount());
    }

    public void update(View view, ReqChooseLeaders event) {
        model.getJoinedGame(view).ifPresent(gameContext -> {
            gameContext.chooseLeaders(view, model.getPlayer(view), null);
        });
    }

    public void update(View view, ReqChooseResources event) {
        model.getJoinedGame(view).ifPresent(gameContext -> {
            gameContext.chooseResources(view, model.getPlayer(view), null);
        });
    }

    public void update(View view, ReqSwapShelves event) {
        model.getJoinedGame(view).ifPresent(gameContext -> {
            gameContext.swapShelves(view, model.getPlayer(view), null, null);
        });
    }

    public void update(View view, ReqActivateLeader event) {
        model.getJoinedGame(view).ifPresent(gameContext -> {
            gameContext.activateLeader(view, model.getPlayer(view), null);
        });
    }

    public void update(View view, ReqDiscardLeader event) {
        model.getJoinedGame(view).ifPresent(gameContext -> {
            gameContext.discardLeader(view, model.getPlayer(view), null);
        });
    }

    public void update(View view, ReqTakeFromMarket event) {
        model.getJoinedGame(view).ifPresent(gameContext -> {
            gameContext.takeMarketResources(view, model.getPlayer(view), event.isRow(), event.getIndex(), null, null);
        });
    }

    public void update(View view, ReqBuyDevCard event) {
        model.getJoinedGame(view).ifPresent(gameContext -> {
            gameContext.buyDevCard(view, model.getPlayer(view), null, event.getLevel(), event.getSlotIndex(), null);
        });
    }

    public void update(View view, ReqActivateProduction event) {
        model.getJoinedGame(view).ifPresent(gameContext -> {
            gameContext.activateProductionGroup(view, model.getPlayer(view), null);
        });
    }

    public void update(View view, ReqTurnEnd event) {
        model.getJoinedGame(view).ifPresent(gameContext -> {
            gameContext.endTurn(view, model.getPlayer(view));
        });
    }
}
