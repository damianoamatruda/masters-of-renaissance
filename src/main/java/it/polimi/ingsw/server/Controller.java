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

    public void update(View view, ReqJoin event) {
        model.joinLobby(view, event.getNickname());
    }

    public void update(View view, ReqPlayersCount event) {
        model.setCountToNewGame(view, event.getCount());
    }

    public void update(View view, ReqChooseLeaders event) {
        model.checkJoinedThen(view, (gameContext, nickname) ->
                gameContext.chooseLeaders(view, nickname, null));
    }

    public void update(View view, ReqChooseResources event) {
        model.checkJoinedThen(view, (gameContext, nickname) ->
                gameContext.chooseResources(view, nickname, null));
    }

    public void update(View view, ReqSwapShelves event) {
        model.checkJoinedThen(view, (gameContext, nickname) ->
                gameContext.swapShelves(view, nickname, null, null));
    }

    public void update(View view, ReqActivateLeader event) {
        model.checkJoinedThen(view, (gameContext, nickname) ->
                gameContext.activateLeader(view, nickname, null));
    }

    public void update(View view, ReqDiscardLeader event) {
        model.checkJoinedThen(view, (gameContext, nickname) ->
                gameContext.discardLeader(view, nickname, null));
    }

    public void update(View view, ReqTakeFromMarket event) {
        model.checkJoinedThen(view, (gameContext, nickname) ->
                gameContext.takeMarketResources(view, nickname, event.isRow(), event.getIndex(), null, null));
    }

    public void update(View view, ReqBuyDevCard event) {
        model.checkJoinedThen(view, (gameContext, nickname) ->
                gameContext.buyDevCard(view, nickname, null, event.getLevel(), event.getSlotIndex(), null));
    }

    public void update(View view, ReqActivateProduction event) {
        model.checkJoinedThen(view, (gameContext, nickname) ->
                gameContext.activateProductionGroup(view, nickname, null));
    }

    public void update(View view, ReqTurnEnd event) {
        model.checkJoinedThen(view, (gameContext, nickname) ->
                gameContext.endTurn(view, nickname));
    }
}
