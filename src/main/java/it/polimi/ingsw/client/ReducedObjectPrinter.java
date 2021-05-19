package it.polimi.ingsw.client;

import it.polimi.ingsw.common.reducedmodel.*;

public interface ReducedObjectPrinter {
    public void update(ReducedActionToken newObject);
    public void update(ReducedCardRequirement newObject);
    public void update(ReducedDepotLeaderCard newObject);
    public void update(ReducedDevCard newObject);
    public void update(ReducedDevCardGrid newObject);
    public void update(ReducedDevCardRequirement newObject);
    public void update(ReducedDevCardRequirementEntry newObject);
    public void update(ReducedDiscountLeaderCard newObject);
    public void update(ReducedGame newObject);
    public void update(ReducedLeaderCard newObject);
    public void update(ReducedMarket newObject);
    public void update(ReducedPlayerSetup newObject);
    public void update(ReducedProductionLeaderCard newObject);
    public void update(ReducedProductionRequest newObject);
    public void update(ReducedResourceContainer newObject);
    public void update(ReducedResourceRequirement newObject);
    public void update(ReducedResourceTransactionRecipe newObject);
}
