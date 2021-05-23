package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.ReducedObjectPrinter;
import it.polimi.ingsw.client.ViewModel.ViewModel;
import it.polimi.ingsw.common.reducedmodel.*;

import java.util.*;

public class CliReducedObjectPrinter implements ReducedObjectPrinter {
    private final Cli cli;
    private final ViewModel viewModel;

    public CliReducedObjectPrinter(Cli cli, ViewModel viewModel) {
        this.cli = cli;
        this.viewModel = viewModel;
    }

    @Override
    public void update(ReducedActionToken newObject) {
        cli.getOut().printf("ActionToken ID: %d, kind: %s%n",
                newObject.getId(),
                newObject.getKind()
        );
        cli.getOut().println(newObject.getDiscardedDevCardColor() == null ? "" :
                String.format("Color of discarded development card: %s\n", printColor(newObject.getDiscardedDevCardColor())));
    }

    @Override
    public void update(ReducedDevCard newObject) {
        cli.getOut().printf("[Development] ID: %d, color: %s%n",
                newObject.getId(),
                printColor(newObject.getColor())
        );
        cli.getOut().printf("Level: %d, VP: %d%n",
                newObject.getLevel(),
                newObject.getVictoryPoints()
        );
        cli.getOut().printf("Production ID: %d%n",
                newObject.getProduction()
        );
        cli.getOut().println("Requirements (resources):");

        newObject.getCost()
                .getRequirements().forEach((key, value) -> cli.getOut().println(printResource(key) + ": " + value));

        cli.getOut().println();
        Optional<ReducedResourceTransactionRecipe> prod = viewModel.getGameData().getProduction(newObject.getProduction());
        prod.ifPresent(this::update);

        cli.getOut().println();
    }

    @Override
    public void update(ReducedDevCardGrid newObject) {
        cli.getOut().println("Development card grid state:");
        newObject.getGrid().forEach((key, value) -> cli.getOut().println(printColor(key) + ": " + value.stream().filter(Objects::nonNull).map(Stack::peek).toList()));

        List<Integer> topCards = new ArrayList<>();
        newObject.getGrid().forEach((key, value) -> topCards.addAll(value.stream().filter(Objects::nonNull).map(Stack::peek).toList()));

        cli.getOut().println();
        topCards.forEach(id -> viewModel.getGameData().getDevelopmentCard(id).ifPresent(this::update));
    }

    @Override
    public void update(ReducedLeaderCard newObject) {
        cli.getOut().printf("[Leader] ID: %d, type: %s%n",
                newObject.getId(),
                newObject.getLeaderType()
        );
        cli.getOut().printf("BoundResource: %s, VP: %d%n",
                newObject.getResourceType(),
                newObject.getVictoryPoints()
        );
        cli.getOut().printf("Active status: %s, depot ID: %d%n",
                newObject.isActive(),
                newObject.getContainerId()
        );
        cli.getOut().printf("Production ID: %d, discount: %d%n",
                newObject.getProduction(),
                newObject.getDiscount()
        );
        if (newObject.getDevCardRequirement() != null) {
            cli.getOut().println("Requirements (development cards):");
            newObject.getDevCardRequirement().getEntries()
                    .forEach(e -> cli.getOut().println("Color: " + printColor(e.getColor()) + ", level: " + e.getLevel() + ", amount: " + e.getAmount()));
        }
        if (newObject.getResourceRequirement() != null) {
            cli.getOut().println("Requirements (resources):");
            newObject.getResourceRequirement().getRequirements().forEach((key, value) -> cli.getOut().println(printResource(key) + ": " + value));
        }

        cli.getOut().println();

        Optional<ReducedResourceTransactionRecipe> prod = viewModel.getGameData().getProduction(newObject.getProduction());
        prod.ifPresent(this::update);
    }

    @Override
    public void update(ReducedMarket newObject) {
        cli.getOut().println("Market:");
        cli.getOut().print("╔");
        cli.getOut().print("═".repeat(43));
        cli.getOut().println("╗");

        for (int i = 0; i < newObject.getGrid().size(); i++) {
            List<String> r = newObject.getGrid().get(i);
            cli.getOut().print("║");
            for (int j = 0; j < r.size(); j++) {
                String res = r.get(j);
                cli.getOut().printf("%-22s", Cli.centerLine(printResource(res), 22));
                if (j < r.size() - 1) cli.getOut().print(" │");
                else cli.getOut().print(" ");
            }
            cli.getOut().print("║");
            cli.getOut().println();
            if (i < newObject.getGrid().size() - 1) {
                cli.getOut().print("║");
                cli.getOut().print(("─".repeat(10) + "┼").repeat(r.size() - 1) + "─".repeat(10));
                cli.getOut().println("║");

            }
        }

        cli.getOut().println("╠" + "═".repeat(5) + "╦" + "═".repeat(37) + "╝");

        cli.getOut().println("║" + " ".repeat(5) + "╚" + "═".repeat(37) + "╗");
        cli.getOut().print("║" + " ".repeat(33));
        cli.getOut().printf("%-23s", Cli.centerLine(printResource(newObject.getSlide()), 23));
        cli.getOut().println("║");
        cli.getOut().println("╚" + "═".repeat(43) + "╝");
//        cli.getOut().println("Slide resource: " + printResource(newObject.getSlide()));
        cli.getOut().println("Replaceable resource type: " + printResource(newObject.getReplaceableResType()) + "\n");
    }

    @Override
    public void update(ReducedResourceContainer newObject) {
        cli.getOut().printf("Resource Container ID: %d, bounded resource: %s, dimensions: %d%n",
                newObject.getId(), printResource(newObject.getboundedResType()), newObject.getDimensions());

        newObject.getContent().forEach((key, value) -> cli.getOut().println(printResource(key) + ": " + value));
        cli.getOut().println();
    }
    
    @Override
    public void update(ReducedResourceTransactionRecipe newObject) {
        cli.getOut().printf("Production ID: %d%n",
                newObject.getId());

        cli.getOut().println("Input:");
        newObject.getInput().forEach((key1, value1) -> cli.getOut().println(printResource(key1) + ": " + value1));

        cli.getOut().printf("Input blanks: %d%n",
                newObject.getInputBlanks());

        cli.getOut().println("Input blanks exclusions:");
        newObject.getInputBlanksExclusions().forEach(e -> cli.getOut().print(e + ", "));

        cli.getOut().println("Output:");
        newObject.getOutput().forEach((key, value) -> cli.getOut().println(printResource(key) + ": " + value));

        cli.getOut().printf("Output blanks: %d%n",
                newObject.getOutputBlanks());

        cli.getOut().println("Output blanks exclusions:");
        newObject.getInputBlanksExclusions().forEach(e -> cli.getOut().print(printResource(e) + ", "));

        cli.getOut().println(newObject.isDiscardableOutput() ? "Output is discardable\n" : "");
    }

    @Override
    public void showPlayers(List<String> nicknames) {
        cli.getOut().println("Players:");
        nicknames.forEach(n -> cli.getOut().print(n + ", "));
    }

    @Override
    public void showCurrentPlayer(String player) {
        cli.getOut().println("Current player: " + player);
    }

    @Override
    public void showBaseProductions(Map<String, Integer> baseProductions) {
        cli.getOut().println("Base productions:");

        baseProductions.forEach((key, value) -> cli.getOut().println("Player: " + key + ", baseprod ID: " + value));
    }

    @Override
    public void showLeadersHand(String player, int leaderId) {
//        cli.getOut().printf("Leader %d now owned by player %s.%n", leaderId, player);
        // print actual leader
    }

    @Override
    public void showWarehouseShelves(String player) {
        cli.getOut().printf("Showing %s's shelves (depot leaders' included):%n", player);

        viewModel.getPlayerShelves(player).forEach(this::update);
    }

    @Override
    public void showStrongbox(String player) {
        cli.getOut().printf("Showing %s's strongbox:%n", player);

        viewModel.getGameData().getContainer(viewModel.getPlayerData(player).getStrongbox()).ifPresent(this::update);
    }

    @Override
    public void showPlayerSlots(String player) {
        cli.getOut().println("Showing" + player + "'s development card slots:");
        
        viewModel.getPlayerData(player).getDevSlots().stream()
            .map(slot -> slot.get(0))
            .map(id -> viewModel.getGameData().getDevelopmentCard(id).orElse(null))
            .filter(Objects::nonNull)
            .forEach(this::update);
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
                cli.getOut().printf(rowTemplate, row.toArray());
            }
            cli.getOut().println("\n");

        }
    }

    public void printCardGrid(ReducedDevCardGrid grid) {
        List<List<ReducedDevCard>> topCards = new ArrayList<>();
        int j = 0;

        for(String key : grid.getGrid().keySet()) {
            cli.trackSlimLine();
            topCards.add(
                grid.getGrid().get(key).stream()
                    .filter(Objects::nonNull)
                    .map(Stack::peek)
                    .map(id -> viewModel.getGameData().getDevelopmentCard(id).orElse(null))
                    .filter(Objects::nonNull)
                    .toList());
            
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
                rowTemplate += "%-50s │";
            }
            rowTemplate += "\n";

            int length = lines.stream().map(List::size).reduce(Integer::max).orElse(0);
            for(int k = 0; k < length; k++) {
                List<String> row = new ArrayList<>();
                for (int i = 0; i < topCards.get(j).size(); i++) {
                    if (k < lines.get(i).size())
                        row.add(lines.get(i).get(k));
                    else row.add("");
                }
                cli.getOut().printf(rowTemplate, row.toArray());
            }
            j++;
        }
        cli.trackSlimLine();
        cli.getOut().println();

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
        Optional<ReducedResourceTransactionRecipe> r = viewModel.getGameData().getProduction(reducedLeaderCard.getProduction());
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
        String color = viewModel.getGameData().getDevCardColors().stream().filter(c -> c.getName().equals(colorName)).map(ReducedColor::getcolorValue).findAny().orElseThrow();
        return "\u001B[1m" + color + colorName + "\u001B[0m"; // "⚫"
    }

    private String printResource(String resourceType) {
        if(resourceType == null)  return "Ø";
        String color = viewModel.getGameData().getResourceTypes().stream().filter(c -> c.getName().equals(resourceType)).map(ReducedResourceType::getcolorValue).findAny().orElseThrow();
        return "\u001B[1m" + color + resourceType + "\u001B[0m";
    }

}
