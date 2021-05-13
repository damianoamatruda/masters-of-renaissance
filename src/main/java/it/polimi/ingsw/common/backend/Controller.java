package it.polimi.ingsw.common.backend;

import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.backend.model.Lobby;
import it.polimi.ingsw.common.events.vcevents.*;

public class Controller {
    private final Lobby model;

    public Controller(Lobby model) {
        this.model = model;
    }

    public void update(View view, ReqGoodbye event) {
        model.exit(view);
    }

    public void update(View view, ReqJoin event) {
        model.joinLobby(view, event.getNickname());
    }

    public void update(View view, ReqNewGame event) {
        model.prepareNewGame(view, event.getPlayersCount());
    }

    public void update(View view, ReqChooseLeaders event) {
        model.checkJoinedThen(view, (gameContext, nickname) ->
                gameContext.chooseLeaders(view, nickname, event.getLeaders()));
    }

    public void update(View view, ReqChooseResources event) {
        model.checkJoinedThen(view, (gameContext, nickname) ->
                gameContext.chooseResources(view, nickname, event.getShelves()));
    }

    public void update(View view, ReqSwapShelves event) {
        model.checkJoinedThen(view, (gameContext, nickname) ->
                gameContext.swapShelves(view, nickname, event.getShelf1(), event.getShelf2()));
    }

    public void update(View view, ReqActivateLeader event) {
        model.checkJoinedThen(view, (gameContext, nickname) ->
                gameContext.activateLeader(view, nickname, event.getLeader()));
    }

    public void update(View view, ReqDiscardLeader event) {
        model.checkJoinedThen(view, (gameContext, nickname) ->
                gameContext.discardLeader(view, nickname, event.getLeader()));
    }

    public void update(View view, ReqTakeFromMarket event) {
        model.checkJoinedThen(view, (gameContext, nickname) ->
                gameContext.takeMarketResources(view, nickname, event.isRow(), event.getIndex(), event.getReplacements(), event.getShelves()));
    }

    public void update(View view, ReqBuyDevCard event) {
        model.checkJoinedThen(view, (gameContext, nickname) ->
                gameContext.buyDevCard(view, nickname, event.getColor(), event.getLevel(), event.getDevSlot(), event.getResContainers()));
    }

    public void update(View view, ReqActivateProduction event) {
        model.checkJoinedThen(view, (gameContext, nickname) ->
                gameContext.activateProductionRequests(view, nickname, event.getProdRequests()));
    }

    public void update(View view, ReqEndTurn event) {
        model.checkJoinedThen(view, (gameContext, nickname) ->
                gameContext.endTurn(view, nickname));
    }
}
