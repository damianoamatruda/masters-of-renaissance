package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.ReducedObjectPrinter;
import it.polimi.ingsw.common.reducedmodel.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CliReducedObjectPrinter implements ReducedObjectPrinter {
    private final ReducedGame cache;

    public CliReducedObjectPrinter(ReducedGame cache) {
        this.cache = cache;
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
        System.out.println(String.format("Production ID: %d",
            newObject.getProduction()
        ));
        System.out.println("Requirements (resources):");

        newObject.getCost()
                .getRequirements().entrySet().stream()
                .forEach(e -> System.out.println("Resource type: " + e.getKey() + ", amount: " + e.getValue()));

        System.out.println();
        Optional<ReducedResourceTransactionRecipe> prod = cache.getProduction(newObject.getProduction());
        if (prod.isPresent())
            update(prod.get());

        System.out.println();
    }

    @Override
    public void update(ReducedDevCardGrid newObject) {
        System.out.println("Development card grid state:");
        newObject.getGrid().entrySet().stream().forEach(e -> System.out.println(e.getKey() + ": " + e.getValue().stream().filter(s -> s != null).map(s -> s.peek()).toList()));
        
        List<Integer> topCards = new ArrayList<>();
        newObject.getGrid().entrySet().stream().forEach(e -> topCards.addAll(e.getValue().stream().filter(s -> s != null).map(s -> s.peek()).toList()));

        System.out.println();
        topCards.forEach(id -> update(cache.getDevCard(id)));
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
            newObject.getDevCardRequirement().getEntries().stream()
                .forEach(e -> System.out.println("Color: " + e.getColor() + ", level: " + e.getLevel() + ", amount: " + e.getAmount()));
        }
        if (newObject.getResourceRequirement() != null) {
            System.out.println("Requirements (resources):");
            newObject.getResourceRequirement().getRequirements().entrySet().stream()
                    .forEach(e -> System.out.println("Resource type: " + e.getKey() + ", amount: " + e.getValue()));
        }

        System.out.println();

        Optional<ReducedResourceTransactionRecipe> prod = cache.getProduction(newObject.getProduction());
        if (prod.isPresent())
            update(prod.get());
    }

    @Override
    public void update(ReducedMarket newObject) {
        System.out.println("Market:");
        newObject.getGrid().forEach(r -> {
            r.forEach(res -> System.out.print(printResource(res) + "\t"));
            System.out.print("\n");
        });
        System.out.print("\n");
        System.out.println("Slide resource: " + newObject.getSlide());
        System.out.println("Replaceable resource type: " + newObject.getReplaceableResType() + "\n");
    }

    @Override
    public void update(ReducedResourceContainer newObject) {
        System.out.println(String.format("Resource Container ID: %d, bounded resource: %s, dimensions: %d",
                newObject.getId(), newObject.getboundedResType(), newObject.getDimensions()));

        newObject.getContent().entrySet().stream().forEach(e -> System.out.println("Resource type: " + e.getKey() + ", amount: " + e.getValue()));
        System.out.println();
    }
    
    @Override
    public void update(ReducedResourceTransactionRecipe newObject) {
        System.out.println(String.format("Production ID: %d",
            newObject.getId()));

        System.out.println("Input:");
        newObject.getInput().entrySet().stream().forEach(e -> System.out.println("Resource type: " + e.getKey() + ", amount: " + e.getValue()));

        System.out.println(String.format("Input blanks: %d",
            newObject.getInputBlanks()));
        
        System.out.println("Input blanks exclusions:");
        newObject.getInputBlanksExclusions().forEach(e -> System.out.print(e + ", "));

        System.out.println("Output:");
        newObject.getOutput().entrySet().stream().forEach(e -> System.out.println("Resource type: " + e.getKey() + ", amount: " + e.getValue()));

        System.out.println(String.format("Output blanks: %d",
            newObject.getOutputBlanks()));
        
        System.out.println("Output blanks exclusions:");
        newObject.getInputBlanksExclusions().forEach(e -> System.out.print(e + ", "));

        System.out.println(newObject.isDiscardableOutput() ? "Output is discardable\n" : "");
    }

    @Override
    public void showPlayers(List<String> nicknames) {
        System.out.println("Players:");
        nicknames.forEach(n -> System.out.print(n + ", "));
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

    @Override
    public void showLeadersHand(String player, int leaderId) {
        System.out.println(String.format("Leader %d now owned by player %s.", leaderId, player));
    }

    @Override
    public void showWarehouseShelves(String player) {
        System.out.println(String.format("Showing %s's warehouse shelves:", player));
        cache.getPlayerWarehouseShelvesIDs(player).forEach(s -> {
            Optional<ReducedResourceContainer> cont = cache.getContainers().stream().filter(c -> c.getId() == s).findFirst();
            if (cont.isPresent())
                update(cont.get());
        });

        System.out.println("\nShowing leader shelves:");

        cache.getLeaderCards().stream()
            .filter(lc -> lc.getContainerId() >= 0 &&
                          cache.getPlayerLeaders(cache.getNickname()).contains(lc.getId()) &&
                          lc.isActive())
            .forEach(lc -> update(cache.getContainers().stream().filter(c -> c.getId() == lc.getContainerId()).findFirst().get()));
    }

    @Override
    public void showStrongbox(String player) {
        System.out.println(String.format("Showing %s's strongbox:", player));

        int id = cache.getPlayerStrongboxID(player);

        Optional<ReducedResourceContainer> cont = cache.getContainers().stream().filter(c -> c.getId() == id).findFirst();
        if (cont.isPresent())
            cont.get().getContent().entrySet().stream().forEach(e -> System.out.println(e.getKey() + ": " + e.getValue()));
    }

    @Override
    public void showPlayerSlots(String player) {
        System.out.println("Showing" + player + "'s development card slots:");
        if (cache.getPlayerDevSlots(player) != null)
            cache.getPlayerDevSlots(player).entrySet().stream().forEach(e -> System.out.println("Slot " + e.getKey() + ", card ID: " + e.getValue()));
    }

    private String printColor(String colorName) {
        String color = cache.getColors().stream().filter(c -> c.getName().equals(colorName)).map(ReducedColor::getcolorValue).findAny().orElseThrow();
        return color + "⚫" + "\u001B[0m";
    }

    private String printResource(String resourceType) {
        String color = cache.getResourceTypes().stream().filter(c -> c.getName().equals(resourceType)).map(ReducedResourceType::getcolorValue).findAny().orElseThrow();
        return color + "⚫" + "\u001B[0m";
    }
}
