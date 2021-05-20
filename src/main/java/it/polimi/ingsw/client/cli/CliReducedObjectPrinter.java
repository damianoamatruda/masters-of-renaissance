package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.ReducedObjectPrinter;
import it.polimi.ingsw.common.events.mvevents.*;
import it.polimi.ingsw.common.reducedmodel.*;

public class CliReducedObjectPrinter implements ReducedObjectPrinter {
    private final Cli cli; // probably unneeded but who knows
    private final ReducedGame model;

    public CliReducedObjectPrinter(Cli cli, ReducedGame model) {
        this.cli = cli;
        this.model = model;
    }

    @Override
    public void update(ReducedActionToken newObject) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void update(ReducedCardRequirement newObject) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void update(ReducedDevCard newObject) {
        
    }

    @Override
    public void update(ReducedDevCardGrid newObject) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void update(ReducedDevCardRequirement newObject) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void update(ReducedDevCardRequirementEntry newObject) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void update(ReducedGame newObject) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void update(ReducedLeaderCard newObject) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void update(ReducedMarket newObject) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void update(ReducedPlayerSetup newObject) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void update(ReducedProductionRequest newObject) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void update(ReducedResourceContainer newObject) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void update(ReducedResourceRequirement newObject) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void update(ReducedResourceTransactionRecipe newObject) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void update(ResWelcome newObject) {
        System.out.println(Cli.center("WELCOME"));
    }
}
