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
            try {
                gameContext.chooseLeaders(model.getPlayer(view), null);
                System.out.println("Chose Leaders.");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void update(View view, ReqChooseResources event) {
        model.getJoinedGame(view).ifPresent(gameContext -> {
            try {
                gameContext.chooseResources(model.getPlayer(view), null);
                System.out.println("Chose initial resources.");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void update(View view, ReqSwapShelves event) {
        model.getJoinedGame(view).ifPresent(gameContext -> {
            try {
                gameContext.swapShelves(model.getPlayer(view), null, null);
                System.out.println("Swapped shelves.");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void update(View view, ReqActivateLeader event) {
        model.getJoinedGame(view).ifPresent(gameContext -> {
            try {
                gameContext.activateLeader(model.getPlayer(view), null);
                System.out.println("Activated Leader.");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void update(View view, ReqDiscardLeader event) {
        model.getJoinedGame(view).ifPresent(gameContext -> {
            try {
                gameContext.discardLeader(model.getPlayer(view), null);
                System.out.println("Discarded leader.");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void update(View view, ReqTakeFromMarket event) {
        model.getJoinedGame(view).ifPresent(gameContext -> {
            try {
                gameContext.takeMarketResources(model.getPlayer(view), event.isRow(), event.getIndex(), null, null);
                System.out.println("Took market resources.");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void update(View view, ReqBuyDevCard event) {
        model.getJoinedGame(view).ifPresent(gameContext -> {
            try {
                gameContext.buyDevCard(model.getPlayer(view), null, event.getLevel(), event.getSlotIndex(), null);
                System.out.println("Bought development card.");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void update(View view, ReqActivateProduction event) {
        model.getJoinedGame(view).ifPresent(gameContext -> {
            try {
                gameContext.activateProductionGroup(model.getPlayer(view), null);
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
