package it.polimi.ingsw.client;

import java.util.List;
import java.util.Map;

import it.polimi.ingsw.common.reducedmodel.*;

public interface ReducedObjectPrinter {
    void update(ReducedActionToken newObject);
    void update(ReducedDevCard newObject);
    void update(ReducedDevCardGrid newObject);
    void update(ReducedLeaderCard newObject);
    void update(ReducedMarket newObject);
    void update(ReducedResourceContainer newObject);
    void update(ReducedResourceTransactionRecipe newObject);
    
    void showPlayers(List<String> nicknames);
    void showCurrentPlayer(String player);
    void showBaseProductions(Map<String, Integer> baseProductions);
    void showLeadersHand(String player, int leaderId);
    void showWarehouseShelves(String player);
    void showStrongbox(String player);
}
