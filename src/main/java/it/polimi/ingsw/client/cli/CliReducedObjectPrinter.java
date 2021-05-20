package it.polimi.ingsw.client.cli;

import java.util.List;
import java.util.Map;
import java.util.Stack;

import it.polimi.ingsw.client.ReducedObjectPrinter;
import it.polimi.ingsw.common.events.mvevents.*;
import it.polimi.ingsw.common.reducedmodel.*;

public class CliReducedObjectPrinter implements ReducedObjectPrinter {
    private final Cli cli;

    public CliReducedObjectPrinter(Cli cli) {
        this.cli = cli;
    }

    private static String stringifyDevCardRequirement(ReducedDevCardRequirement req) {
        String out = "";

        req.getEntries().stream().forEach(e -> out.concat("Color: " + e.getColor() + ", level: " + e.getLevel() + ", amount: " + e.getAmount() + "\n"));
        
        return out;
    }

    private static String stringifyResoureRequirement(ReducedResourceRequirement req) {
        String out = "";

        req.getRequirements().entrySet().stream().forEach(e -> out.concat("Resource type: " + e.getKey() + ", amount: " + Integer.toString(e.getValue()) + "\n"));
        return out;
    }

    @Override
    public void update(ReducedActionToken newObject) {
        System.out.println(String.format("ActionToken ID: %d, kind: %s",
            newObject.getId(),
            newObject.getKind()
        ));
        System.out.println(newObject.getDiscardedDevCardColor() == null ? "" : 
            String.format("Color of discarded development card: %s\n", newObject.getDiscardedDevCardColor()));
    }

    @Override
    public void update(ReducedDevCard newObject) {
        System.out.println(String.format("Development Card ID: %d, color: %s",
            newObject.getId(),
            newObject.getColor()
        ));
        System.out.println(String.format("Level: %d, VP: %d",
            newObject.getLevel(),
            newObject.getVictoryPoints()
        ));
        System.out.println(String.format("Production ID: %d\n",
            newObject.getProduction()
        ));
        System.out.println("Requirements (resources):");
        System.out.println(stringifyResoureRequirement(newObject.getCost()));

        System.out.println();
    }

    @Override
    public void update(ReducedDevCardGrid newObject) {
        List<Stack<Integer>> grid = newObject.getGrid().entrySet().stream().flatMap(e -> e.getValue().stream()).toList();

        System.out.println("Development card grid state:");
        newObject.getGrid().entrySet().stream().forEach(e -> System.out.println(e.getKey() + ": " + e.getValue().stream().filter(s -> s != null).map(s -> s.peek()).toList()));
    }

    @Override
    public void update(ReducedLeaderCard newObject) {
        System.out.println(String.format("Leader Card ID: %d, type: %s",
            newObject.getId(),
            newObject.getLeaderType()
        ));
        System.out.println(String.format("BoundResource: %s, VP: %d",
            newObject.getResourceType(),
            newObject.getVictoryPoints()
        ));
        System.out.println(String.format("Active status: %s, depot ID: %d",
            newObject.isActive(),
            newObject.getContainerId()
        ));
        System.out.println(String.format("Production ID: %d, discount: %d",
            newObject.getProduction(),
            newObject.getDiscount()
        ));
        if (newObject.getDevCardRequirement() != null) {
            System.out.println("Requirements (development cards):");
            System.out.println(stringifyDevCardRequirement(newObject.getDevCardRequirement()));
        }
        if (newObject.getResourceRequirement() != null) {
            System.out.println("Requirements (resources):");
            System.out.println(stringifyResoureRequirement(newObject.getResourceRequirement()));
        }

        System.out.println();
    }

    @Override
    public void update(ReducedMarket newObject) {
        System.out.println("Market:");
        newObject.getGrid().forEach(r -> {
            r.forEach(res -> System.out.print(res + " "));
            System.out.print("\n");
        });
        System.out.println("Slide resource: " + newObject.getSlide());
        System.out.println("Replaceable resource type: " + newObject.getReplaceableResType() + "\n");
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
        System.out.println(String.format("Resource Container ID: %d, bounded resource: %s, dimensions: %d\n",
            newObject.getId(), newObject.getboundedResType(), newObject.getDimensions()));

        String contents = "";
        newObject.getContent().entrySet().stream().forEach(e -> contents.concat("Resource type: " + e.getKey() + ", amount: " + Integer.toString(e.getValue())));
        System.out.println(contents);
    }
    
    @Override
    public void update(ReducedResourceTransactionRecipe newObject) {
        System.out.println(String.format("Production ID: %d\n",
            newObject.getId()));

        String input = "", output = "";
        newObject.getInput().entrySet().stream().forEach(e -> input.concat("Resource type: " + e.getKey() + ", amount: " + Integer.toString(e.getValue()) + "\n"));
        System.out.println("Input:\n" + input);

        System.out.println(String.format("Input blanks: %d\n",
            newObject.getInputBlanks()));
        
        System.out.println("Input blanks exclusions:\n");
        newObject.getInputBlanksExclusions().forEach(e -> System.out.print(e + ", "));

        newObject.getOutput().entrySet().stream().forEach(e -> output.concat("Resource type: " + e.getKey() + ", amount: " + Integer.toString(e.getValue()) + "\n"));
        System.out.println("Output:\n" + output);

        System.out.println(String.format("Output blanks: %d\n",
            newObject.getOutputBlanks()));
        
        System.out.println("Output blanks exclusions:\n");
        newObject.getInputBlanksExclusions().forEach(e -> System.out.print(e + ", "));

        System.out.println(newObject.isDiscardableOutput() ? "Output is discardable\n" : "");
    }

    @Override
    public void update(ResWelcome newObject) {
        System.out.println(Cli.center("WELCOME"));
    }

    @Override
    public void showPlayers(List<String> nicknames) {
        nicknames.forEach(n -> System.out.println(n));
    }

    @Override
    public void showCurrentPlayer(String player) {
        System.out.println("Current player: " + player);
    }

    @Override
    public void showBaseProductions(Map<String, Integer> baseProductions) {
        System.out.println("Base productions:");

        baseProductions.entrySet().stream().forEach(e -> System.out.println("Player: " + e.getKey() + ", baseprod ID: " + e.getValue()));
    }
}
