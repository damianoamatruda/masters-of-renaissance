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
        System.out.printf("[Development] ID: %d, color: %s%n",
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
                .getRequirements().forEach((key, value) -> System.out.println(printResource(key) + ": " + value));

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
        System.out.printf("[Leader] ID: %d, type: %s%n",
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
            newObject.getResourceRequirement().getRequirements().forEach((key, value) -> System.out.println(printResource(key) + ": " + value));
        }

        System.out.println();

        Optional<ReducedResourceTransactionRecipe> prod = cache.getProduction(newObject.getProduction());
        prod.ifPresent(this::update);
    }

    @Override
    public void update(ReducedMarket newObject) {
        System.out.println("Market:");
        System.out.print("╔");
        for(int i = 0; i < 40; i++) System.out.print("═");
        System.out.println("╗");

        newObject.getGrid().forEach(r -> {
            System.out.print("║");
            r.forEach(res -> System.out.printf("%-23s", Cli.centerLine(printResource(res), 23)));
            System.out.print("║");
            System.out.println();
        });

        System.out.print("╚");
        for(int i = 0; i < 40; i++) System.out.print("═");
        System.out.print("╝");

        System.out.print("\n");
        System.out.println("Slide resource: " + printResource(newObject.getSlide()));
        System.out.println("Replaceable resource type: " + printResource(newObject.getReplaceableResType()) + "\n");
    }

    @Override
    public void update(ReducedResourceContainer newObject) {
        System.out.printf("Resource Container ID: %d, bounded resource: %s, dimensions: %d%n",
                newObject.getId(), printResource(newObject.getboundedResType()), newObject.getDimensions());

        newObject.getContent().forEach((key, value) -> System.out.println(printResource(key) + ": " + value));
        System.out.println();
    }
    
    @Override
    public void update(ReducedResourceTransactionRecipe newObject) {
        System.out.printf("Production ID: %d%n",
            newObject.getId());

        System.out.println("Input:");
        newObject.getInput().forEach((key1, value1) -> System.out.println(printResource(key1) + ": " + value1));

        System.out.printf("Input blanks: %d%n",
            newObject.getInputBlanks());
        
        System.out.println("Input blanks exclusions:");
        newObject.getInputBlanksExclusions().forEach(e -> System.out.print(e + ", "));

        System.out.println("Output:");
        newObject.getOutput().forEach((key, value) -> System.out.println(printResource(key) + ": " + value));

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
//        System.out.printf("Leader %d now owned by player %s.%n", leaderId, player);
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

    public void printOwnedLeaders(List<ReducedLeaderCard> leaders) {
        for(int i = 0; i < leaders.size(); i += 3) {
            List<List<String>> rows = new ArrayList<>();

            List<ReducedLeaderCard> cards = new ArrayList<>();
            for(int j = 0; j < 3 && j < leaders.size() - i; j++) {
                cards.add(leaders.get(i + j));
                rows.add(new ArrayList<>());
            }

            for(int j = 0; j < 3 && j < leaders.size() - i; j++) {
                List<String> column = rows.get(j);
                ReducedLeaderCard reducedLeaderCard = cards.get(j);

                column.add(String.format("%-63s", String.format("[Leader] ID: \u001B[1m\u001B[97m%d\u001B[0m, type: %s",
                        reducedLeaderCard.getId(),
                        reducedLeaderCard.getLeaderType())));
                column.add(String.format("%-63s", String.format("BoundResource: %s, VP: %d",
                        printResource(reducedLeaderCard.getResourceType()),
                        reducedLeaderCard.getVictoryPoints())));
                column.add(String.format("Active status: %s, depot ID: %d",
                        reducedLeaderCard.isActive(),
                        reducedLeaderCard.getContainerId()));
                column.add(String.format("Production ID: %d, discount: %d",
                        reducedLeaderCard.getProduction(),
                        reducedLeaderCard.getDiscount()));

                addRequirementsToPrinter(column, reducedLeaderCard);
                addProductionToPrinter(column, reducedLeaderCard);
            }

            String rowTemplate = "";
            for(int j = 0; j < 3 && j < leaders.size() - i; j++) {
                rowTemplate += "%-50s ";
            }
            rowTemplate += "\n";

            int length = rows.stream().map(List::size).reduce(Integer::max).orElse(0);
            for(int k = 0; k < length; k++) {
                List<String> row = new ArrayList<>();
                for(int j = 0; j < 3 && j < leaders.size() - i; j++) {
                    if(k < rows.get(j).size())
                        row.add(rows.get(j).get(k));
                    else row.add("");
                }
                System.out.printf(rowTemplate, row.toArray());
            }
            System.out.println("\n");

        }
    }

    public void printCardGrid(ReducedDevCardGrid grid) {
        List<List<ReducedDevCard>> topCards = new ArrayList<>();
        int j = 0;

        for(String key : grid.getGrid().keySet()) {
            Cli.trackSlimLine();
            topCards.add(grid.getGrid().get(key).stream().filter(Objects::nonNull).map(Stack::peek).map(cache::getDevCard).toList());
            List<List<String>> lines = new ArrayList<>();
            for(int i = 0; i < topCards.get(j).size(); i++) {
                ReducedDevCard card = topCards.get(j).get(i);
                lines.add(new ArrayList<>());
                List<String> column = lines.get(i);
                column.add(String.format("%-76s", String.format("[Development] ID: \u001B[1m\u001B[97m%d\u001B[0m, color: %s",
                        card.getId(),
                        printColor(card.getColor())
                )));
                column.add(String.format("Level: %d, VP: %d",
                        card.getLevel(),
                        card.getVictoryPoints()
                ));
                column.add("Requirements (resources):");
                card.getCost()
                        .getRequirements().forEach((k, value) ->column.add(String.format("%-63s", printResource(k) + ": " + value)));
                addProductionToPrinter(column, card);
            }

            String rowTemplate = "";
            for(int i = 0; i < topCards.get(j).size(); i++) {
                rowTemplate += "%-50s |";
            }
            rowTemplate += "\n";

            int length = lines.stream().map(List::size).reduce(Integer::max).orElse(0);
            for(int k = 0; k < length; k++) {
                List<String> row = new ArrayList<>();
                for(int i = 0; i < topCards.get(j).size(); i++) {
                    if(k < lines.get(i).size())
                        row.add(lines.get(i).get(k));
                    else row.add("");
                }
                System.out.printf(rowTemplate, row.toArray());
            }
            j++;
        }
        Cli.trackSlimLine();
        System.out.println();

    }

    private void addRequirementsToPrinter(List<String> column, ReducedLeaderCard reducedLeaderCard) {
        if (reducedLeaderCard.getDevCardRequirement() != null) {
            column.add("Requirements (development cards):");
            for(int k = 0; k < reducedLeaderCard.getDevCardRequirement().getEntries().size(); k++) {
                ReducedDevCardRequirementEntry e = reducedLeaderCard.getDevCardRequirement().getEntries().get(k);
                String req = String.format("%-63s", "Color: " + printColor(e.getColor()) + ", level: " + e.getLevel() + ", amount: " + e.getAmount());
                column.add(req);
            }
        }
        if(reducedLeaderCard.getResourceRequirement() != null) {
            column.add("Requirements (resources):");
            List<String> keys = reducedLeaderCard.getResourceRequirement().getRequirements().keySet().stream().toList();
            for(String key : keys) {
                String req = String.format("%-63s", String.format("%s, %d", printResource(key), reducedLeaderCard.getResourceRequirement().getRequirements().get(key)));
                column.add(req);
            }
        }
    }

    private void addProductionToPrinter(List<String> column, ReducedCard reducedLeaderCard) {
        Optional<ReducedResourceTransactionRecipe> r = cache.getProduction(reducedLeaderCard.getProduction());
        if (r.isPresent()) {
            column.add(String.format("Production ID: %d",
                    r.get().getId()));
            column.add("Input:");
            r.get().getInput().forEach((key1, value1) -> column.add(String.format("%-63s", printResource(key1) + ": " + value1)));
            column.add(String.format("Input blanks: %d",
                    r.get().getInputBlanks()));
            column.add("Input blanks exclusions:");
            r.get().getInputBlanksExclusions().forEach(e -> column.add(e + ", "));
            column.add("Output:");
            r.get().getOutput().forEach((key, value) -> column.add(String.format("%-63s", printResource(key) + ": " + value)));
            column.add(String.format("Output blanks: %d",
                    r.get().getOutputBlanks()));
            column.add("Output blanks exclusions:");
            r.get().getInputBlanksExclusions().forEach(e -> column.add(printResource(e) + ", "));
            column.add(r.get().isDiscardableOutput() ? "Output is discardable" : " ");
        }
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

}
