package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.controller.messages.*;
import it.polimi.ingsw.server.model.Lobby;
import it.polimi.ingsw.server.view.View;

public class Controller {
    private final Lobby model;

    public Controller(Lobby model) {
        this.model = model;
    }

    public void update(View view, ReqQuit message) {
        model.exit(view);
    }

    public void update(View view, ReqNickname message) {
        model.joinLobby(view, message.getNickname());
    }

    public void update(View view, ReqPlayersCount message) {
        model.setCountToNewGame(view, message.getCount());
    }

    public void update(View view, ReqChooseLeaders message) {
        model.getJoinedGame(view).ifPresent(gameContext -> {
            try {
                gameContext.chooseLeaders(model.getPlayer(view), message.getLeaders());
                System.out.println("Chose Leaders.");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void update(View view, ReqChooseResources message) {
        model.getJoinedGame(view).ifPresent(gameContext -> {
            try {
                gameContext.chooseResources(model.getPlayer(view), message.getShelves());
                System.out.println("Chose initial resources.");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void update(View view, ReqSwapShelves message) {
        model.getJoinedGame(view).ifPresent(gameContext -> {
            try {
                gameContext.swapShelves(model.getPlayer(view), message.getS1(), message.getS2());
                System.out.println("Swapped shelves.");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void update(View view, ReqActivateLeader message) {
        model.getJoinedGame(view).ifPresent(gameContext -> {
            try {
                gameContext.activateLeader(model.getPlayer(view), message.getLeader());
                System.out.println("Activated Leader.");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void update(View view, ReqDiscardLeader message) {
        model.getJoinedGame(view).ifPresent(gameContext -> {
            try {
                gameContext.discardLeader(model.getPlayer(view), message.getLeader());
                System.out.println("Discarded leader.");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void update(View view, ReqTakeFromMarket message) {
        model.getJoinedGame(view).ifPresent(gameContext -> {
            try {
                gameContext.takeMarketResources(model.getPlayer(view), message.isRow(), message.getIndex(), message.getReplacements(), message.getShelves());
                System.out.println("Took market resources.");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void update(View view, ReqBuyDevCard message) {
        model.getJoinedGame(view).ifPresent(gameContext -> {
            try {
                gameContext.buyDevCard(model.getPlayer(view), message.getColor(), message.getLevel(), message.getSlotIndex(), message.getResContainers());
                System.out.println("Bought development card.");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void update(View view, ReqActivateProduction message) {
        model.getJoinedGame(view).ifPresent(gameContext -> {
            try {
                gameContext.activateProductionGroup(model.getPlayer(view), message.getProductionGroup());
                System.out.println("Activated production.");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void update(View view, ReqTurnEnd message) {
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
