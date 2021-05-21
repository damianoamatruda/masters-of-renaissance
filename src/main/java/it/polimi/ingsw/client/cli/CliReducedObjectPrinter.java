package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.ReducedObjectPrinter;
import it.polimi.ingsw.common.reducedmodel.*;

import java.util.*;

public class CliReducedObjectPrinter implements ReducedObjectPrinter {
    private final ReducedGame cache;

    public CliReducedObjectPrinter(ReducedGame cache) {
        this.cache = cache;
    }

    @Override
    public void update(ReducedActionToken newObject) {
        System.out.printf("ActionToken ID: %d, kind: %s%n",
            newObject.getId(),
            newObject.getKind()
        );
        System.out.println(newObject.getDiscardedDevCardColor() == null ? "" : 
            String.format("Color of discarded development card: %s\n", printColor(newObject.getDiscardedDevCardColor())));
    }

    @Override
    public void update(ReducedDevCard newObject) {
        System.out.printf("Development Card ID: %d, color: %s%n",
            newObject.getId(),
                printColor(newObject.getColor())
        );
        System.out.printf("Level: %d, VP: %d%n",
            newObject.getLevel(),
            newObject.getVictoryPoints()
        );
        System.out.printf("Production ID: %d%n",
            newObject.getProduction()
        );
        System.out.println("Requirements (resources):");

        newObject.getCost()
                .getRequirements().forEach((key, value) -> System.out.println("Resource type: " + printResource(key) + ", amount: " + value));

        System.out.println();
        Optional<ReducedResourceTransactionRecipe> prod = cache.getProduction(newObject.getProduction());
        prod.ifPresent(this::update);

        System.out.println();
    }

    @Override
    public void update(ReducedDevCardGrid newObject) {
        System.out.println("Development card grid state:");
        newObject.getGrid().forEach((key, value) -> System.out.println(printColor(key) + ": " + value.stream().filter(Objects::nonNull).map(Stack::peek).toList()));
        
        List<Integer> topCards = new ArrayList<>();
        newObject.getGrid().forEach((key, value) -> topCards.addAll(value.stream().filter(Objects::nonNull).map(Stack::peek).toList()));

        System.out.println();
        topCards.forEach(id -> update(cache.getDevCard(id)));
    }

    @Override
    public void update(ReducedLeaderCard newObject) {
        System.out.printf("Leader Card ID: %d, type: %s%n",
            newObject.getId(),
            newObject.getLeaderType()
        );
        System.out.printf("BoundResource: %s, VP: %d%n",
            newObject.getResourceType(),
            newObject.getVictoryPoints()
        );
        System.out.printf("Active status: %s, depot ID: %d%n",
            newObject.isActive(),
            newObject.getContainerId()
        );
        System.out.printf("Production ID: %d, discount: %d%n",
            newObject.getProduction(),
            newObject.getDiscount()
        );
        if (newObject.getDevCardRequirement() != null) {
            System.out.println("Requirements (development cards):");
            newObject.getDevCardRequirement().getEntries()
                .forEach(e -> System.out.println("Color: " + printColor(e.getColor()) + ", level: " + e.getLevel() + ", amount: " + e.getAmount()));
        }
        if (newObject.getResourceRequirement() != null) {
            System.out.println("Requirements (resources):");
            newObject.getResourceRequirement().getRequirements().forEach((key, value) -> System.out.println("Resource type: " + printResource(key) + ", amount: " + value));
        }

        System.out.println();

        Optional<ReducedResourceTransactionRecipe> prod = cache.getProduction(newObject.getProduction());
        prod.ifPresent(this::update);
    }

    @Override
    public void update(ReducedMarket newObject) {
        System.out.println("Market:");
        System.out.print("╔");
        for(int i = 0; i < 35; i++) System.out.print("═");
        System.out.println("╗");

        newObject.getGrid().forEach(r -> {
            System.out.print("║\t");
            r.forEach(res -> System.out.print(printResource(res) + "\t"));
            System.out.print("║");
            System.out.println();
        });

        System.out.print("╚");
        for(int i = 0; i < 35; i++) System.out.print("═");
        System.out.print("╝");

        System.out.print("\n");
        System.out.println("Slide resource: " + printResource(newObject.getSlide()));
        System.out.println("Replaceable resource type: " + printResource(newObject.getReplaceableResType()) + "\n");
    }

    @Override
    public void update(ReducedResourceContainer newObject) {
        System.out.printf("Resource Container ID: %d, bounded resource: %s, dimensions: %d%n",
                newObject.getId(), printResource(newObject.getboundedResType()), newObject.getDimensions());

        newObject.getContent().forEach((key, value) -> System.out.println("Resource type: " + printResource(key) + ", amount: " + value));
        System.out.println();
    }
    
    @Override
    public void update(ReducedResourceTransactionRecipe newObject) {
        System.out.printf("Production ID: %d%n",
            newObject.getId());

        System.out.println("Input:");
        newObject.getInput().forEach((key1, value1) -> System.out.println("Resource type: " + printResource(key1) + ", amount: " + value1));

        System.out.printf("Input blanks: %d%n",
            newObject.getInputBlanks());
        
        System.out.println("Input blanks exclusions:");
        newObject.getInputBlanksExclusions().forEach(e -> System.out.print(e + ", "));

        System.out.println("Output:");
        newObject.getOutput().forEach((key, value) -> System.out.println("Resource type: " + printResource(key) + ", amount: " + value));

        System.out.printf("Output blanks: %d%n",
            newObject.getOutputBlanks());
        
        System.out.println("Output blanks exclusions:");
        newObject.getInputBlanksExclusions().forEach(e -> System.out.print(printResource(e) + ", "));

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

        baseProductions.forEach((key, value) -> System.out.println("Player: " + key + ", baseprod ID: " + value));
    }

    @Override
    public void showLeadersHand(String player, int leaderId) {
        System.out.printf("Leader %d now owned by player %s.%n", leaderId, player);
        // print actual leader
    }

    @Override
    public void showWarehouseShelves(String player) {
        System.out.printf("Showing %s's warehouse shelves:%n", player);
        cache.getPlayerWarehouseShelvesIDs(player).forEach(s -> {
            Optional<ReducedResourceContainer> cont = cache.getContainers().stream().filter(c -> c.getId() == s).findFirst();
            cont.ifPresent(this::update);
        });

        System.out.println("\nShowing leader shelves:");

        cache.getLeaderCards().stream()
            .filter(lc -> lc.getContainerId() >= 0 &&
                          cache.getPlayerLeadersId(cache.getNickname()).contains(lc.getId()) &&
                          lc.isActive())
            .forEach(lc -> update(cache.getContainers().stream().filter(c -> c.getId() == lc.getContainerId()).findFirst().orElseThrow()));
    }

    @Override
    public void showStrongbox(String player) {
        System.out.printf("Showing %s's strongbox:%n", player);

        int id = cache.getPlayerStrongboxID(player);

        Optional<ReducedResourceContainer> cont = cache.getContainers().stream().filter(c -> c.getId() == id).findFirst();
        cont.ifPresent(reducedResourceContainer -> reducedResourceContainer.getContent().forEach((key, value) -> System.out.println(printResource(key) + ": " + value)));
    }

    @Override
    public void showPlayerSlots(String player) {
        System.out.println("Showing" + player + "'s development card slots:");
        if (cache.getPlayerDevSlots(player) != null)
            cache.getPlayerDevSlots(player).forEach((key, value) -> System.out.println("Slot " + key + ", card ID: " + value));
    }

    private String printColor(String colorName) {
        if(colorName == null) return /*"\u001B[1m" +*/ "Ø" /*+ "\u001B[0m"*/;
        String color = cache.getColors().stream().filter(c -> c.getName().equals(colorName)).map(ReducedColor::getcolorValue).findAny().orElseThrow();
        return "\u001B[1m" + color + colorName + "\u001B[0m"; // "⚫"
    }

    private String printResource(String resourceType) {
        if(resourceType == null)  return "Ø";
        String color = cache.getResourceTypes().stream().filter(c -> c.getName().equals(resourceType)).map(ReducedResourceType::getcolorValue).findAny().orElseThrow();
        return "\u001B[1m" + color + resourceType + "\u001B[0m";
    }

    public void printOwnedLeaders(List<ReducedLeaderCard> leaders) {
        for(int i = 0; i < leaders.size(); i += 3) {
            List<ReducedLeaderCard> row = new ArrayList<>();
            for(int j = 0; j < 3 && j < leaders.size() - i; j++) {
                row.add(leaders.get(i + j));
            }
            String columnTemplate = "";
            List<String> format = new ArrayList<>();
            for(int j = 0; j < row.size(); j++) {
                columnTemplate += "%-50s ";
            }
            columnTemplate += "\n";

            for (ReducedLeaderCard reducedLeaderCard : row) {
                format.add(String.format("Leader Card ID: %d, type: %s",
                        reducedLeaderCard.getId(),
                        reducedLeaderCard.getLeaderType()));
            }
            System.out.printf(columnTemplate, format.toArray());
            format.clear();

            for (ReducedLeaderCard reducedLeaderCard : row) {
                String res = String.format("%-63s", String.format("BoundResource: %s, VP: %d",
                        printResource(reducedLeaderCard.getResourceType()),
//                        reducedLeaderCard.getResourceType(),
                        reducedLeaderCard.getVictoryPoints()));
                format.add(res);
            }
            System.out.printf(columnTemplate, format.toArray());
            format.clear();

            for (ReducedLeaderCard reducedLeaderCard : row) {
                format.add(String.format("Active status: %s, depot ID: %d",
                        reducedLeaderCard.isActive(),
                        reducedLeaderCard.getContainerId()));
            }
            System.out.printf(columnTemplate, format.toArray());
            format.clear();

            for (ReducedLeaderCard reducedLeaderCard : row) {
                format.add(String.format("Production ID: %d, discount: %d",
                        reducedLeaderCard.getProduction(),
                        reducedLeaderCard.getDiscount()));
            }
            System.out.printf(columnTemplate, format.toArray());
            format.clear();

            for (ReducedLeaderCard reducedLeaderCard : row) {
                if (reducedLeaderCard.getDevCardRequirement() != null)
                    format.add("Requirements (development cards):");
                else format.add("Requirements (resources):");
            }
            System.out.printf(columnTemplate, format.toArray());
            format.clear();

            int length = row.stream()
                    .map(l -> l.getResourceRequirement() != null ? l.getResourceRequirement().getRequirements().keySet().size() : l.getDevCardRequirement().getEntries().size())
                    .reduce(Integer::max).orElseThrow();
            for(int j = 0; j < length; j++) {
                for (ReducedLeaderCard reducedLeaderCard : row) {
                    if (reducedLeaderCard.getDevCardRequirement() != null) {
                        ReducedDevCardRequirementEntry e = reducedLeaderCard.getDevCardRequirement().getEntries().get(j);
                        if(e != null) {
                            String req = String.format("%-63s", "Color: " + printColor(e.getColor()) + ", level: " + e.getLevel() + ", amount: " + e.getAmount());
                            format.add(req);
                        }
                        else format.add(" ");
                    }
                    else {
                        List<String> keys = reducedLeaderCard.getResourceRequirement().getRequirements().keySet().stream().toList();
                        if(j < keys.size()) {
                            String key = keys.get(j);
                            String req = String.format("%-63s", String.format("Resource type: %s, amount: %d", printResource(key), reducedLeaderCard.getResourceRequirement().getRequirements().get(key)));
                            format.add(req);
                        }
                        else
                            format.add(" ");
                    }
                }
                System.out.printf(columnTemplate, format.toArray());
                format.clear();
            }
            // Productions yet to be printed

            System.out.println();
//
//            Optional<ReducedResourceTransactionRecipe> prod = cache.getProduction(newObject.getProduction());
//            prod.ifPresent(this::update);
            System.out.println("\n");
        }
    }
}
