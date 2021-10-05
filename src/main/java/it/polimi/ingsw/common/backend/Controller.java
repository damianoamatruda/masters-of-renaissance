package it.polimi.ingsw.common.backend;

import it.polimi.ingsw.common.EventListener;
import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.backend.model.Lobby;
import it.polimi.ingsw.common.events.vcevents.*;

import java.util.HashMap;
import java.util.Map;

public class Controller {
    private final Lobby model;

    private final Map<View, EventListener<ReqActivateProductions>> reqActivateProductionEventListeners = new HashMap<>();
    private final Map<View, EventListener<ReqBuyDevCard>> reqBuyDevCardEventListeners = new HashMap<>();
    private final Map<View, EventListener<ReqChooseLeaders>> reqChooseLeadersEventListeners = new HashMap<>();
    private final Map<View, EventListener<ReqChooseResources>> reqChooseResourcesEventListeners = new HashMap<>();
    private final Map<View, EventListener<ReqEndTurn>> reqEndTurnEventListeners = new HashMap<>();
    private final Map<View, EventListener<ReqJoin>> reqJoinEventListeners = new HashMap<>();
    private final Map<View, EventListener<ReqLeaderAction>> reqLeaderActionEventListeners = new HashMap<>();
    private final Map<View, EventListener<ReqNewGame>> reqNewGameEventListeners = new HashMap<>();
    private final Map<View, EventListener<ReqQuit>> reqQuitEventListeners = new HashMap<>();
    private final Map<View, EventListener<ReqSwapShelves>> reqSwapShelvesEventListeners = new HashMap<>();
    private final Map<View, EventListener<ReqTakeFromMarket>> reqTakeFromMarketEventListeners = new HashMap<>();

    public Controller(Lobby model) {
        this.model = model;
    }

    public void registerOnVC(View view) {
        view.addEventListener(ReqActivateProductions.class, reqActivateProductionEventListeners.computeIfAbsent(view, v -> event -> on(v, event)));
        view.addEventListener(ReqBuyDevCard.class, reqBuyDevCardEventListeners.computeIfAbsent(view, v -> event -> on(v, event)));
        view.addEventListener(ReqChooseLeaders.class, reqChooseLeadersEventListeners.computeIfAbsent(view, v -> event -> on(v, event)));
        view.addEventListener(ReqChooseResources.class, reqChooseResourcesEventListeners.computeIfAbsent(view, v -> event -> on(v, event)));
        view.addEventListener(ReqEndTurn.class, reqEndTurnEventListeners.computeIfAbsent(view, v -> event -> on(v, event)));
        view.addEventListener(ReqJoin.class, reqJoinEventListeners.computeIfAbsent(view, v -> event -> on(v, event)));
        view.addEventListener(ReqLeaderAction.class, reqLeaderActionEventListeners.computeIfAbsent(view, v -> event -> on(v, event)));
        view.addEventListener(ReqNewGame.class, reqNewGameEventListeners.computeIfAbsent(view, v -> event -> on(v, event)));
        view.addEventListener(ReqQuit.class, reqQuitEventListeners.computeIfAbsent(view, v -> event -> on(v, event)));
        view.addEventListener(ReqSwapShelves.class, reqSwapShelvesEventListeners.computeIfAbsent(view, v -> event -> on(v, event)));
        view.addEventListener(ReqTakeFromMarket.class, reqTakeFromMarketEventListeners.computeIfAbsent(view, v -> event -> on(v, event)));
    }

    public void unregisterOnVC(View view) {
        view.removeEventListener(ReqActivateProductions.class, reqActivateProductionEventListeners.remove(view));
        view.removeEventListener(ReqBuyDevCard.class, reqBuyDevCardEventListeners.remove(view));
        view.removeEventListener(ReqChooseLeaders.class, reqChooseLeadersEventListeners.remove(view));
        view.removeEventListener(ReqChooseResources.class, reqChooseResourcesEventListeners.remove(view));
        view.removeEventListener(ReqEndTurn.class, reqEndTurnEventListeners.remove(view));
        view.removeEventListener(ReqJoin.class, reqJoinEventListeners.remove(view));
        view.removeEventListener(ReqLeaderAction.class, reqLeaderActionEventListeners.remove(view));
        view.removeEventListener(ReqNewGame.class, reqNewGameEventListeners.remove(view));
        view.removeEventListener(ReqQuit.class, reqQuitEventListeners.remove(view));
        view.removeEventListener(ReqSwapShelves.class, reqSwapShelvesEventListeners.remove(view));
        view.removeEventListener(ReqTakeFromMarket.class, reqTakeFromMarketEventListeners.remove(view));
    }

    private void on(View view, ReqQuit event) {
        unregisterOnVC(view);
        model.quit(view);
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

    private void on(View view, ReqLeaderAction event) {
        if (event.isActivate())
            model.checkJoinedThen(view, (gameContext, nickname) ->
                    gameContext.activateLeader(view, nickname, event.getLeader()));
        else
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

    private void on(View view, ReqActivateProductions event) {
        model.checkJoinedThen(view, (gameContext, nickname) ->
                gameContext.activateProductionRequests(view, nickname, event.getProdRequests()));
    }

    private void on(View view, ReqEndTurn event) {
        model.checkJoinedThen(view, (gameContext, nickname) ->
                gameContext.endTurn(view, nickname));
    }
}
