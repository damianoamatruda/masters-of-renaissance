package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.controller.events.*;
import it.polimi.ingsw.server.model.Lobby;
import it.polimi.ingsw.server.view.View;

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
            try {
                gameContext.chooseLeaders(model.getPlayer(view), event.getLeaders());
                System.out.println("Chose Leaders.");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void update(View view, ReqChooseResources event) {
        model.getJoinedGame(view).ifPresent(gameContext -> {
            try {
                gameContext.chooseResources(model.getPlayer(view), event.getShelves());
                System.out.println("Chose initial resources.");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void update(View view, ReqSwapShelves event) {
        model.getJoinedGame(view).ifPresent(gameContext -> {
            try {
                gameContext.swapShelves(model.getPlayer(view), event.getS1(), event.getS2());
                System.out.println("Swapped shelves.");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void update(View view, ReqActivateLeader event) {
        model.getJoinedGame(view).ifPresent(gameContext -> {
            try {
                gameContext.activateLeader(model.getPlayer(view), event.getLeader());
                System.out.println("Activated Leader.");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void update(View view, ReqDiscardLeader event) {
        model.getJoinedGame(view).ifPresent(gameContext -> {
            try {
                gameContext.discardLeader(model.getPlayer(view), event.getLeader());
                System.out.println("Discarded leader.");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void update(View view, ReqTakeFromMarket event) {
        model.getJoinedGame(view).ifPresent(gameContext -> {
            try {
                gameContext.takeMarketResources(model.getPlayer(view), event.isRow(), event.getIndex(), event.getReplacements(), event.getShelves());
                System.out.println("Took market resources.");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void update(View view, ReqBuyDevCard event) {
        model.getJoinedGame(view).ifPresent(gameContext -> {
            try {
                gameContext.buyDevCard(model.getPlayer(view), event.getColor(), event.getLevel(), event.getSlotIndex(), event.getResContainers());
                System.out.println("Bought development card.");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void update(View view, ReqActivateProduction event) {
        model.getJoinedGame(view).ifPresent(gameContext -> {
            try {
                gameContext.activateProductionGroup(model.getPlayer(view), event.getProductionGroup());
                System.out.println("Activated production.");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void update(View view, ReqTurnEnd event) {
        model.getJoinedGame(view).ifPresent(gameContext -> {
            try {
                gameContext.endTurn(model.getPlayer(view));
                System.out.println("Ended turn.");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
