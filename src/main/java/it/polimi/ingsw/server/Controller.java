package it.polimi.ingsw.server;

import it.polimi.ingsw.common.ControllerObservable;
import it.polimi.ingsw.common.ModelObserver;
import it.polimi.ingsw.common.events.*;
import it.polimi.ingsw.server.model.Lobby;

public class Controller {
    private final Lobby model;

    public Controller(Lobby model) {
        this.model = model;
    }

    public void update(ControllerObservable view, ReqQuit event) {
        model.exit(((ModelObserver) view));
    }

    public void update(ControllerObservable view, ReqNickname event) {
        model.joinLobby(((ModelObserver) view), event.getNickname());
    }

    public void update(ControllerObservable view, ReqPlayersCount event) {
        model.setCountToNewGame(((ModelObserver) view), event.getCount());
    }

    public void update(ControllerObservable view, ReqChooseLeaders event) {
        model.getJoinedGame(((ModelObserver) view)).ifPresent(gameContext -> {
            try {
                gameContext.chooseLeaders(model.getPlayer(((ModelObserver) view)), null);
                System.out.println("Chose Leaders.");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void update(ControllerObservable view, ReqChooseResources event) {
        model.getJoinedGame(((ModelObserver) view)).ifPresent(gameContext -> {
            try {
                gameContext.chooseResources(model.getPlayer(((ModelObserver) view)), null);
                System.out.println("Chose initial resources.");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void update(ControllerObservable view, ReqSwapShelves event) {
        model.getJoinedGame(((ModelObserver) view)).ifPresent(gameContext -> {
            try {
                gameContext.swapShelves(model.getPlayer(((ModelObserver) view)), null, null);
                System.out.println("Swapped shelves.");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void update(ControllerObservable view, ReqActivateLeader event) {
        model.getJoinedGame(((ModelObserver) view)).ifPresent(gameContext -> {
            try {
                gameContext.activateLeader(model.getPlayer(((ModelObserver) view)), null);
                System.out.println("Activated Leader.");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void update(ControllerObservable view, ReqDiscardLeader event) {
        model.getJoinedGame(((ModelObserver) view)).ifPresent(gameContext -> {
            try {
                gameContext.discardLeader(model.getPlayer(((ModelObserver) view)), null);
                System.out.println("Discarded leader.");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void update(ControllerObservable view, ReqTakeFromMarket event) {
        model.getJoinedGame(((ModelObserver) view)).ifPresent(gameContext -> {
            try {
                gameContext.takeMarketResources(model.getPlayer(((ModelObserver) view)), event.isRow(), event.getIndex(), null, null);
                System.out.println("Took market resources.");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void update(ControllerObservable view, ReqBuyDevCard event) {
        model.getJoinedGame(((ModelObserver) view)).ifPresent(gameContext -> {
            try {
                gameContext.buyDevCard(model.getPlayer(((ModelObserver) view)), null, event.getLevel(), event.getSlotIndex(), null);
                System.out.println("Bought development card.");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void update(ControllerObservable view, ReqActivateProduction event) {
        model.getJoinedGame(((ModelObserver) view)).ifPresent(gameContext -> {
            try {
                gameContext.activateProductionGroup(model.getPlayer(((ModelObserver) view)), null);
                System.out.println("Activated production.");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void update(ControllerObservable view, ReqTurnEnd event) {
        model.getJoinedGame(((ModelObserver) view)).ifPresent(gameContext -> {
            try {
                gameContext.endTurn(model.getPlayer(((ModelObserver) view)));
                System.out.println("Ended turn.");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
