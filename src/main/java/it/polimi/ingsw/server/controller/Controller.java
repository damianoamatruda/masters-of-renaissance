package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.model.DevCardColor;
import it.polimi.ingsw.server.model.Lobby;
import it.polimi.ingsw.server.model.ProductionGroup;
import it.polimi.ingsw.server.model.leadercards.LeaderCard;
import it.polimi.ingsw.server.model.resourcecontainers.ResourceContainer;
import it.polimi.ingsw.server.model.resourcecontainers.Shelf;
import it.polimi.ingsw.server.model.resourcetypes.ResourceType;
import it.polimi.ingsw.server.view.View;

import java.util.List;
import java.util.Map;

public class Controller {
    private final Lobby model;

    public Controller(Lobby model) {
        this.model = model;
    }

    public void quit(View view) {
        view.updateGoodbye();
    }

    public void registerNickname(View view, String nickname) {
        model.joinLobby(view, nickname);
    }

    public void setPlayersCount(View view, int count) {
        model.setCountToNewGame(view, count);
    }

    public void getMarket(View view) {
        // TODO: This should be a response, integrated in ResGameStarted.
        model.getJoinedGame(view).ifPresent(gameContext -> {
            try {
                gameContext.getMarket();
                System.out.println("Got market.");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void chooseLeaders(View view, List<LeaderCard> leaders) {
        model.getJoinedGame(view).ifPresent(gameContext -> {
            try {
                gameContext.chooseLeaders(model.getPlayer(view), leaders);
                System.out.println("Chose Leaders.");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void chooseResources(View view, Map<ResourceContainer, Map<ResourceType, Integer>> shelves) {
        model.getJoinedGame(view).ifPresent(gameContext -> {
            try {
                gameContext.chooseResources(model.getPlayer(view), shelves);
                System.out.println("Chose initial resources.");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void swapShelves(View view, Shelf s1, Shelf s2) {
        model.getJoinedGame(view).ifPresent(gameContext -> {
            try {
                gameContext.swapShelves(model.getPlayer(view), s1, s2);
                System.out.println("Swapped shelves.");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void activateLeader(View view, LeaderCard leader) {
        model.getJoinedGame(view).ifPresent(gameContext -> {
            try {
                gameContext.activateLeader(model.getPlayer(view), leader);
                System.out.println("Activated Leader.");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void discardLeader(View view, LeaderCard leader) {
        model.getJoinedGame(view).ifPresent(gameContext -> {
            try {
                gameContext.discardLeader(model.getPlayer(view), leader);
                System.out.println("Discarded leader.");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void takeMarketResources(View view, boolean isRow, int index, Map<ResourceType, Integer> replacements,
                                    Map<ResourceContainer, Map<ResourceType, Integer>> shelves) {
        model.getJoinedGame(view).ifPresent(gameContext -> {
            try {
                gameContext.takeMarketResources(model.getPlayer(view), isRow, index, replacements, shelves);
                System.out.println("Took market resources.");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void buyDevCard(View view, DevCardColor color, int level, int slotIndex,
                           Map<ResourceContainer, Map<ResourceType, Integer>> resContainers) {
        model.getJoinedGame(view).ifPresent(gameContext -> {
            try {
                gameContext.buyDevCard(model.getPlayer(view), color, level, slotIndex, resContainers);
                System.out.println("Bought development card.");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void activateProductionGroup(View view, ProductionGroup productionGroup) {
        model.getJoinedGame(view).ifPresent(gameContext -> {
            try {
                gameContext.activateProductionGroup(model.getPlayer(view), productionGroup);
                System.out.println("Activated production.");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void endTurn(View view) {
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
