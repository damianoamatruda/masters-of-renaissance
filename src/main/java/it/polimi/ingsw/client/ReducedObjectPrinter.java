package it.polimi.ingsw.client;

import java.util.List;

import it.polimi.ingsw.common.events.mvevents.ResWelcome;
import it.polimi.ingsw.common.reducedmodel.*;

public interface ReducedObjectPrinter {
    public void update(ReducedActionToken newObject);
    public void update(ReducedDevCard newObject);
    public void update(ReducedDevCardGrid newObject);
    public void update(ReducedLeaderCard newObject);
    public void update(ReducedMarket newObject);
    public void update(ReducedPlayerSetup newObject);
    public void update(ReducedProductionRequest newObject);
    public void update(ReducedResourceContainer newObject);
    public void update(ReducedResourceTransactionRecipe newObject);
    public void update(ResWelcome newObject);
    
    public void showPlayers(List<String> nicknames);
    public void showCurrentPlayer(String player);
}
