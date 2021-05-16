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
        view.register(ReqGoodbye.class, event -> on(view, event));
        view.register(ReqJoin.class, event -> on(view, event));
        view.register(ReqNewGame.class, event -> on(view, event));
        view.register(ReqChooseLeaders.class, event -> on(view, event));
        view.register(ReqChooseResources.class, event -> on(view, event));
        view.register(ReqSwapShelves.class, event -> on(view, event));
        view.register(ReqActivateLeader.class, event -> on(view, event));
        view.register(ReqDiscardLeader.class, event -> on(view, event));
        view.register(ReqTakeFromMarket.class, event -> on(view, event));
        view.register(ReqBuyDevCard.class, event -> on(view, event));
        view.register(ReqActivateProduction.class, event -> on(view, event));
        view.register(ReqEndTurn.class, event -> on(view, event));
    }

    public void unregisterView(View view) {
        view.unregister(ReqGoodbye.class, event -> on(view, event));
        view.unregister(ReqJoin.class, event -> on(view, event));
        view.unregister(ReqNewGame.class, event -> on(view, event));
        view.unregister(ReqChooseLeaders.class, event -> on(view, event));
        view.unregister(ReqChooseResources.class, event -> on(view, event));
        view.unregister(ReqSwapShelves.class, event -> on(view, event));
        view.unregister(ReqActivateLeader.class, event -> on(view, event));
        view.unregister(ReqDiscardLeader.class, event -> on(view, event));
        view.unregister(ReqTakeFromMarket.class, event -> on(view, event));
        view.unregister(ReqBuyDevCard.class, event -> on(view, event));
        view.unregister(ReqActivateProduction.class, event -> on(view, event));
        view.unregister(ReqEndTurn.class, event -> on(view, event));
    }

    public void on(View view, ReqGoodbye event) {
        model.exit(view);
    }

    public void on(View view, ReqJoin event) {
        model.joinLobby(view, event.getNickname());
    }

    public void on(View view, ReqNewGame event) {
        model.prepareNewGame(view, event.getPlayersCount());
    }

    public void on(View view, ReqChooseLeaders event) {
        model.checkJoinedThen(view, (gameContext, nickname) ->
                gameContext.chooseLeaders(view, nickname, event.getLeaders()));
    }

    public void on(View view, ReqChooseResources event) {
        model.checkJoinedThen(view, (gameContext, nickname) ->
                gameContext.chooseResources(view, nickname, event.getShelves()));
    }

    public void on(View view, ReqSwapShelves event) {
        model.checkJoinedThen(view, (gameContext, nickname) ->
                gameContext.swapShelves(view, nickname, event.getShelf1(), event.getShelf2()));
    }

    public void on(View view, ReqActivateLeader event) {
        model.checkJoinedThen(view, (gameContext, nickname) ->
                gameContext.activateLeader(view, nickname, event.getLeader()));
    }

    public void on(View view, ReqDiscardLeader event) {
        model.checkJoinedThen(view, (gameContext, nickname) ->
                gameContext.discardLeader(view, nickname, event.getLeader()));
    }

    public void on(View view, ReqTakeFromMarket event) {
        model.checkJoinedThen(view, (gameContext, nickname) ->
                gameContext.takeMarketResources(view, nickname, event.isRow(), event.getIndex(), event.getReplacements(), event.getShelves()));
    }

    public void on(View view, ReqBuyDevCard event) {
        model.checkJoinedThen(view, (gameContext, nickname) ->
                gameContext.buyDevCard(view, nickname, event.getColor(), event.getLevel(), event.getDevSlot(), event.getResContainers()));
    }

    public void on(View view, ReqActivateProduction event) {
        model.checkJoinedThen(view, (gameContext, nickname) ->
                gameContext.activateProductionRequests(view, nickname, event.getProdRequests()));
    }

    public void on(View view, ReqEndTurn event) {
        model.checkJoinedThen(view, (gameContext, nickname) ->
                gameContext.endTurn(view, nickname));
    }
}
