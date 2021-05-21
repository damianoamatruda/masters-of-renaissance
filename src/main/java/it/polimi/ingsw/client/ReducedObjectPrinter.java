package it.polimi.ingsw.client;

import java.util.List;
import java.util.Map;

import it.polimi.ingsw.common.events.mvevents.ResWelcome;
import it.polimi.ingsw.common.reducedmodel.*;

public interface ReducedObjectPrinter {
    public void update(ReducedActionToken newObject);
    public void update(ReducedDevCard newObject);
    public void update(ReducedDevCardGrid newObject);
    public void update(ReducedLeaderCard newObject);
    public void update(ReducedMarket newObject);
    public void update(ReducedResourceContainer newObject);
    public void update(ReducedResourceTransactionRecipe newObject);
    public void update(ResWelcome newObject);
    
    public void showPlayers(List<String> nicknames);
    public void showCurrentPlayer(String player);
    public void showBaseProductions(Map<String, Integer> baseProductions);
    public void showLeadersHand(String player, int leaderId);
    public void showWarehouseShelves(String player);
    public void showStrongbox(String player);
}
