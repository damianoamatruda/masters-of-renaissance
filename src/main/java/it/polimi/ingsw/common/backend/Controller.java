package it.polimi.ingsw.common.backend;

import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.backend.model.Lobby;
import it.polimi.ingsw.common.events.vcevents.*;

public class Controller {
    private final Lobby model;

    public Controller(Lobby model) {
        this.model = model;
    }

    public void registerToView(View view) {
        view.addEventListener(ReqGoodbye.class, event -> on(view, event));
        view.addEventListener(ReqJoin.class, event -> on(view, event));
        view.addEventListener(ReqNewGame.class, event -> on(view, event));
        view.addEventListener(ReqChooseLeaders.class, event -> on(view, event));
        view.addEventListener(ReqChooseResources.class, event -> on(view, event));
        view.addEventListener(ReqSwapShelves.class, event -> on(view, event));
        view.addEventListener(ReqActivateLeader.class, event -> on(view, event));
        view.addEventListener(ReqDiscardLeader.class, event -> on(view, event));
        view.addEventListener(ReqTakeFromMarket.class, event -> on(view, event));
        view.addEventListener(ReqBuyDevCard.class, event -> on(view, event));
        view.addEventListener(ReqActivateProduction.class, event -> on(view, event));
        view.addEventListener(ReqEndTurn.class, event -> on(view, event));
    }

    public void unregisterToView(View view) {
        view.removeEventListener(ReqGoodbye.class, event -> on(view, event));
        view.removeEventListener(ReqJoin.class, event -> on(view, event));
        view.removeEventListener(ReqNewGame.class, event -> on(view, event));
        view.removeEventListener(ReqChooseLeaders.class, event -> on(view, event));
        view.removeEventListener(ReqChooseResources.class, event -> on(view, event));
        view.removeEventListener(ReqSwapShelves.class, event -> on(view, event));
        view.removeEventListener(ReqActivateLeader.class, event -> on(view, event));
        view.removeEventListener(ReqDiscardLeader.class, event -> on(view, event));
        view.removeEventListener(ReqTakeFromMarket.class, event -> on(view, event));
        view.removeEventListener(ReqBuyDevCard.class, event -> on(view, event));
        view.removeEventListener(ReqActivateProduction.class, event -> on(view, event));
        view.removeEventListener(ReqEndTurn.class, event -> on(view, event));
    }

    private void on(View view, ReqGoodbye event) {
        model.exit(view);
    }

    private void on(View view, ReqJoin event) {
        model.joinLobby(view, event.getNickname());
    }

    private void on(View view, ReqNewGame event) {
        model.prepareNewGame(view, event.getPlayersCount());
    }

    private void on(View view, ReqChooseLeaders event) {
        model.checkJoinedThen(view, (gameContext, nickname) ->
                gameContext.chooseLeaders(view, nickname, event.getLeaders()));
    }

    private void on(View view, ReqChooseResources event) {
        model.checkJoinedThen(view, (gameContext, nickname) ->
                gameContext.chooseResources(view, nickname, event.getShelves()));
    }

    private void on(View view, ReqSwapShelves event) {
        model.checkJoinedThen(view, (gameContext, nickname) ->
                gameContext.swapShelves(view, nickname, event.getShelf1(), event.getShelf2()));
    }

    private void on(View view, ReqActivateLeader event) {
        model.checkJoinedThen(view, (gameContext, nickname) ->
                gameContext.activateLeader(view, nickname, event.getLeader()));
    }

    private void on(View view, ReqDiscardLeader event) {
        model.checkJoinedThen(view, (gameContext, nickname) ->
                gameContext.discardLeader(view, nickname, event.getLeader()));
    }

    private void on(View view, ReqTakeFromMarket event) {
        model.checkJoinedThen(view, (gameContext, nickname) ->
                gameContext.takeMarketResources(view, nickname, event.isRow(), event.getIndex(), event.getReplacements(), event.getShelves()));
    }

    private void on(View view, ReqBuyDevCard event) {
        model.checkJoinedThen(view, (gameContext, nickname) ->
                gameContext.buyDevCard(view, nickname, event.getColor(), event.getLevel(), event.getDevSlot(), event.getResContainers()));
    }

    private void on(View view, ReqActivateProduction event) {
        model.checkJoinedThen(view, (gameContext, nickname) ->
                gameContext.activateProductionRequests(view, nickname, event.getProdRequests()));
    }

    private void on(View view, ReqEndTurn event) {
        model.checkJoinedThen(view, (gameContext, nickname) ->
                gameContext.endTurn(view, nickname));
    }
}
