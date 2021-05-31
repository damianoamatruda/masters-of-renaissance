package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.ReducedObjectPrinter;
import it.polimi.ingsw.client.viewmodel.ViewModel;
import it.polimi.ingsw.common.reducedmodel.*;

import java.util.*;
import java.util.stream.IntStream;

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

        List<String> cols = new ArrayList<>();
        addResRequirementsToPrinter(cols, newObject.getCost());
        cols.forEach(s -> cli.getOut().println(s));

        cli.getOut().println();
        Optional<ReducedResourceTransactionRecipe> prod = viewModel.getProduction(newObject.getProduction());
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
        topCards.forEach(id -> viewModel.getDevelopmentCard(id).ifPresent(this::update));
    }

    @Override
    public void update(ReducedLeaderCard newObject) {
        cli.getOut().printf("[Leader]%n ID: %d, type: %s%n",
                newObject.getId(),
                newObject.getLeaderType()
        );
        cli.getOut().printf("BoundResource: %s, VP: %d%n",
                newObject.getResourceType(),
                newObject.getVictoryPoints()
        );
        cli.getOut().printf("%-50s", String.format("Active status: %s", newObject.isActive()));

        if(newObject.getContainerId() > -1)
            cli.getOut().printf("%-50s", String.format("Depot ID: %d", newObject.getContainerId()));
        if(newObject.getProduction() > -1)
            cli.getOut().printf("%-50s", String.format("Production ID: %d", newObject.getProduction()));
        if(newObject.getDiscount() > -1)
            cli.getOut().printf("%-50s", String.format("Discount: %d", newObject.getDiscount()));

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

        Optional<ReducedResourceTransactionRecipe> prod = viewModel.getProduction(newObject.getProduction());
        prod.ifPresent(this::update);
    }

    @Override
    public void update(ReducedMarket newObject) {
        int width = newObject.getGrid().stream().map(List::size).reduce(Integer::max).orElse(0);

        cli.getOut().println("Market:");
        cli.getOut().println("Replaceable resource type: " + printResource(newObject.getReplaceableResType()) + "\n");

        cli.getOut().print("╔");
        cli.getOut().print("═".repeat(12 * (width + 1) - 1));
        cli.getOut().println("╗");

        cli.getOut().print("║" + " ".repeat(8));
        cli.getOut().print((String.format("%-10s", " ")).repeat(width) + " ");
        cli.getOut().printf("%-23s", Cli.centerLine(printResource(newObject.getSlide()), 23));
        cli.getOut().println("║");

        cli.getOut().println("║" + " ".repeat(4) + "╔" + "═".repeat(12 * width - 5) + "╦" + "═".repeat(10) + "╝");

        for (int i = 0; i < newObject.getGrid().size(); i++) {
            List<String> r = newObject.getGrid().get(i).stream().map(ReducedResourceType::getName).toList();
            cli.getOut().print("║" + " ".repeat(4) + "║");
            for (int j = 0; j < r.size(); j++) {
                String res = r.get(j);
                cli.getOut().printf("%-22s", Cli.centerLine(printResource(res), 22));
                if (j < r.size() - 1) cli.getOut().print(" │");
                else cli.getOut().print(" ");
            }
            cli.getOut().printf("║ < %d%n", i + 1);
            if (i < newObject.getGrid().size() - 1) {
                cli.getOut().print("║" + " ".repeat(4) + "║");
                cli.getOut().print(("─".repeat(10) + "┼").repeat(r.size() - 1) + "─".repeat(10));
                cli.getOut().println("║");
            }
        }

        cli.getOut().println("╚" + "═".repeat(4) + "╩" + "═".repeat(12 * width - 5) + "╝");
        cli.getOut().print(" ".repeat(6));
        for(int i = 1; i <= width; i++) cli.getOut().printf("%-10s ", Cli.centerLine("^", 10));
        cli.getOut().print("\n" + " ".repeat(6));
        for(int i = 1; i <= width; i++) cli.getOut().printf("%-10s ", Cli.centerLine("" + i, 10));
        System.out.println("\n");

//        cli.getOut().println("Slide resource: " + printResource(newObject.getSlide()));
    }

    @Override
    public void update(ReducedResourceContainer newObject) {
        cli.getOut().printf("Resource Container ID: %d", newObject.getId());
        if (newObject.getboundedResType() != null)
            cli.getOut().printf(", bounded resource: %s", printResource(newObject.getboundedResType()));
        cli.getOut().printf(", size: %d%n", newObject.getSize());

        newObject.getContent().forEach((key, value) -> cli.getOut().println(printResource(key) + ": " + value));
        cli.getOut().println();
    }
    
    @Override
    public void update(ReducedResourceTransactionRecipe newObject) {
        cli.getOut().printf("--- Production ID: %d ---", newObject.getId());
        cli.getOut().println("Input:");
        newObject.getInput().forEach((key1, value1) -> cli.getOut().println(printResource(key1) + ": " + value1));
        if (newObject.getInputBlanks() > 0)
            cli.getOut().printf("Blanks: %d%n", newObject.getInputBlanks());
        if (!newObject.getInputBlanksExclusions().isEmpty()) {
            cli.getOut().println("B. exclusions:");
            newObject.getInputBlanksExclusions().forEach(e -> cli.getOut().print(e + ", "));
        }
        cli.getOut().println("Output:");
        newObject.getOutput().forEach((key, value) -> cli.getOut().println(printResource(key) + ": " + value));
        if (newObject.getOutputBlanks() > 0)
            cli.getOut().printf("Blanks: %d%n", newObject.getOutputBlanks());
        if (!newObject.getInputBlanksExclusions().isEmpty()) {
            cli.getOut().println("Output blanks exclusions:");
            newObject.getInputBlanksExclusions().forEach(e -> cli.getOut().print(printResource(e) + ", "));
        }
        cli.getOut().println(newObject.isDiscardableOutput() ? "Output is discardable" : "");
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

        baseProductions.forEach((key, value) -> cli.getOut().println("Player: " + key));
        List<Integer> baseProds = baseProductions.values().stream().toList();
        for(int i = 0; i < baseProds.size(); i += 3) {
            List<List<String>> rows = new ArrayList<>();

            String rowTemplate = "";
            for(int j = 0; j < 3 && 3 * i + j < baseProds.size() - i; j++) {
                rows.add(new ArrayList<>());
                List<String> column = rows.get(j);
                addProductionToPrinter(column, baseProds.get(3 * i + j));
                rowTemplate += "%-50s ";
            }
            rowTemplate += "\n";

            int length = rows.stream().map(List::size).reduce(Integer::max).orElse(0);
            for(int k = 0; k < length; k++) {
                List<String> row = new ArrayList<>();
                for(int j = 0; j < 3 && 3 * i + j < baseProds.size() - i; j++) {
                    if (k < rows.get(j).size())
                        row.add(rows.get(j).get(k));
                    else row.add("");
                }
                cli.getOut().printf(rowTemplate, row.toArray());
            }
            cli.getOut().println("\n");
        }
    }

    @Override
    public void showWarehouseShelves(String player) {
        cli.getOut().printf("Showing %s's shelves (depot leaders' included):%n", player);

        viewModel.getPlayerShelves(player).forEach(this::update);
    }

    @Override
    public void showStrongbox(String player) {
        cli.getOut().printf("Showing %s's strongbox:%n", player);

        viewModel.getContainer(viewModel.getPlayerData(player).getStrongbox()).ifPresent(this::update);
    }

    @Override
    public void showPlayerSlots(String player) {
        cli.getOut().println("Showing " + player + "'s development card slots:");
//        if (cache.getPlayerDevSlots(player) != null)
//            cache.getPlayerDevSlots(player).forEach((key, value) -> cli.getOut().println("Slot " + key + ", card ID: " + value));
        Map<Integer, ReducedDevCard> slots = new HashMap<>();
//        try {
//            cache.getPlayerDevSlots(player).forEach((key, value) -> slots.put(key, cache.getDevCard(value)));
//            printDevelopmentSlots(slots);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

    }

    public void showLeaders(List<ReducedLeaderCard> leaders) {
        int i;
        for(i = 0; i < leaders.size(); i += 4) {
            cli.getOut().println("─".repeat(40 * Integer.min(4, leaders.size() - i)));
            List<List<String>> rows = new ArrayList<>();

            List<ReducedLeaderCard> cards = new ArrayList<>();
            for(int j = 0; j < 4 && j < leaders.size() - i; j++) {
                cards.add(leaders.get(i + j));
                rows.add(new ArrayList<>());
            }

            for(int j = 0; j < 4 && j < leaders.size() - i; j++) {
                List<String> column = rows.get(j);
                ReducedLeaderCard reducedLeaderCard = cards.get(j);

                column.add(String.format("%-38s", "[Leader]"));

                column.add(String.format("%-51s", String.format("ID: \u001B[1m\u001B[37m%d\u001B[0m, type: %s",
                        reducedLeaderCard.getId(),
                        reducedLeaderCard.getLeaderType()
                )));

                column.add(String.format("%-51s", String.format("BoundResource: %s, VP: %d",
                        printResource(reducedLeaderCard.getResourceType().getName()),
                        reducedLeaderCard.getVictoryPoints())));

                column.add(String.format("%-38s", String.format("Active status: %s", reducedLeaderCard.isActive())));

                if(reducedLeaderCard.getContainerId() > -1)
                    column.add(String.format("%-38s", String.format("Depot ID: %d", reducedLeaderCard.getContainerId())));
                if(reducedLeaderCard.getProduction() > -1)
                    column.add(String.format("%-38s", String.format("Production ID: %d", reducedLeaderCard.getProduction())));
                if(reducedLeaderCard.getDiscount() > -1)
                    column.add(String.format("%-38s", String.format("Discount: %d", reducedLeaderCard.getDiscount())));

                if (reducedLeaderCard.getDevCardRequirement() != null)
                    addCardRequirementsToPrinter(column, reducedLeaderCard.getDevCardRequirement());
                if (reducedLeaderCard.getResourceRequirement() != null)
                    addResRequirementsToPrinter(column, reducedLeaderCard.getResourceRequirement());
                addProductionToPrinter(column, reducedLeaderCard.getProduction());
            }

            String rowTemplate = "";
            for(int j = 0; j < 4 && j < leaders.size() - i; j++) {
                rowTemplate += "%-38s │";
            }
            rowTemplate += "\n";

            int length = rows.stream().map(List::size).reduce(Integer::max).orElse(0);
            for(int k = 0; k < length; k++) {
                List<String> row = new ArrayList<>();
                for(int j = 0; j < 4 && j < leaders.size() - i; j++) {
                    if(k < rows.get(j).size())
                        row.add(rows.get(j).get(k));
                    else row.add("");
                }
                cli.getOut().printf(rowTemplate, row.toArray());
            }
        }
        cli.getOut().println("─".repeat(40 * Integer.min(4, leaders.size() - i + 4)));
        cli.getOut().println("\n");
    }

    public void printCardGrid(ReducedDevCardGrid grid) {
        List<List<ReducedDevCard>> topCards = new ArrayList<>();
        int levels = grid.getLevelsCount();

        for(int i = 1; i <= levels; i++){
            cli.trackSlimLine();
            List<List<String>> lines = new ArrayList<>();
            for (String key : grid.getGrid().keySet()) {
                int index = i;
                ReducedDevCard card = grid.getGrid().get(key).stream()
                    .filter(Objects::nonNull).map(Stack::peek)
                    .map(id -> viewModel.getDevelopmentCard(id).orElse(null))
                    .filter(Objects::nonNull)
                    .filter(c -> c.getLevel() == levels + 1 - index).findAny().orElseThrow();
                topCards.add(new ArrayList<>());
                topCards.get(i - 1).add(card);
            }
            for (int j = 0; j < topCards.get(i - 1).size(); j++) {
                ReducedDevCard card = topCards.get(i - 1).get(j);
                lines.add(new ArrayList<>());
                List<String> column = lines.get(j);
                addDevToPrinter(column, card);
            }

            String rowTemplate = "";
            for (int j = 0; j < topCards.get(i - 1).size(); j++) {
                rowTemplate += "%-38s │";
            }
            rowTemplate += "\n";

            int length = lines.stream().map(List::size).reduce(Integer::max).orElse(0);
            for (int j = 0; j < length; j++) {
                List<String> row = new ArrayList<>();
                for (int l = 0; l < topCards.get(i - 1).size(); l++) {
                    if (j < lines.get(l).size())
                        row.add(lines.get(l).get(j));
                    else row.add("");
                }
                cli.getOut().printf(rowTemplate, row.toArray());
            }
        }
        cli.trackSlimLine();
        cli.getOut().println();

    }

    private void addCardRequirementsToPrinter(List<String> column, ReducedDevCardRequirement requirement) {
        column.add("--- Requirements (dev. cards) ---");
        for(int k = 0; k < requirement.getEntries().size(); k++) {
            ReducedDevCardRequirementEntry e = requirement.getEntries().get(k);
            String req = String.format("  %-49s", e.getAmount() + " " + printColor(e.getColor()) + " card(s) of " +
                    (e.getLevel() > 0 ? ("level " + e.getLevel()) : "any level"));
            column.add(req);
        }
    }

    private void addResRequirementsToPrinter(List<String> column, ReducedResourceRequirement requirement) {
        column.add("--- Requirements (resources) ---");
        List<String> keys = requirement.getRequirements().keySet().stream().toList();
        int i = 0;
        String req = "";
        for(String key : keys) {
            if(i % 2 == 0)
                req = String.format("  %-24s", String.format("%s, %d", printResource(key), requirement.getRequirements().get(key)));
            else {
                req += String.format("  %-36s", String.format("%s, %d", printResource(key), requirement.getRequirements().get(key)));
                column.add(req);
            }
            i++;
        }
        if(keys.size() % 2 != 0){
            req += " ".repeat(25);
            column.add(req);
        }
    }

    private void addProductionToPrinter(List<String> column, int productionID) {
        Optional<ReducedResourceTransactionRecipe> r = viewModel.getProduction(productionID);

        if (r.isPresent()) {
            column.add(String.format("--- Production ID: %d ---", r.get().getId()));
            column.add("Input:");
            r.get().getInput().forEach((key1, value1) -> column.add(String.format("  %-49s", printResource(key1) + ": " + value1)));
            if (r.get().getInputBlanks() > 0)
                column.add(String.format("Blanks: %d", r.get().getInputBlanks()));
            if (!r.get().getInputBlanksExclusions().isEmpty()) {
                column.add("B. exclusions:");
                r.get().getInputBlanksExclusions().forEach(e -> column.add(printResource(e)));
            }
            column.add("Output:");
            r.get().getOutput().forEach((key, value) -> column.add(String.format("  %-49s", printResource(key) + ": " + value)));
            if (r.get().getOutputBlanks() > 0)
                column.add(String.format("Blanks: %d", r.get().getOutputBlanks()));
            if (!r.get().getOutputBlanksExclusions().isEmpty()) {
                column.add("B. exclusions:");
                r.get().getOutputBlanksExclusions().forEach(e -> column.add(printResource(e)));
            }
            column.add(r.get().isDiscardableOutput() ? "Output is discardable" : " ");
        }
    }

    private String printColor(String colorName) {
        if (colorName == null)
            return /*"\u001B[1m" +*/ "Ø" /*+ "\u001B[0m"*/;
        String color = viewModel.getDevCardColors().stream().filter(c -> c.getName().equals(colorName)).map(ReducedColor::getcolorValue).findAny().orElseThrow();
        return "\u001B[1m" + color + colorName + "\u001B[0m"; // "⚫"
    }

    private String printResource(String resourceType) {
        // FIXME: Placeholder for null is now unnecessary
        if (resourceType == null)
            return "Ø";
        String color = viewModel.getResourceTypes().stream().filter(c -> c.getName().equals(resourceType)).map(ReducedResourceType::getColorValue).findAny().orElseThrow();
        return "\u001B[1m" + color + resourceType + "\u001B[0m";
    }

    @Override
    public void printFaithTrack(Map<String, Integer> points) {

        String boldTopLeftCorner = "╔";
        String slimTopLeftCorner = "┌";
        String boldTopRightCorner = "╗";
        String slimTopRightCorner = "┐";
        String boldBottomLeftCorner = "╚";
        String slimBottomLeftCorner = "└";
        String boldBottomRightCorner = "╝";
        String slimBottomRightCorner = "┘";
        String boldT = "╦";
        String slimT = "┬";
        String boldUpwardsT = "╩";
        String slimUpwardsT = "┴";
        String boldHorizontalLine = "═";
        String slimHorizontalLine = "─";
        String boldVerticalLine = "║";
        String slimVerticalLine = "│";

        int cellWidth = /*Integer.max(6, points.keySet().stream().map(String::length).reduce(Integer::max).orElse(0))*/5;
        int maxFaith = viewModel.getFaithTrack().getMaxFaith();
        // Calculate section tiles and yellow tiles, to match the colors
        List<ReducedVaticanSection> sections = viewModel.getFaithTrack().getVaticanSections().values().stream().toList();
        List<Integer> sectionBegins = sections.stream().map(ReducedVaticanSection::getFaithPointsBeginning).sorted().toList();
        List<Integer> sectionEnds = sections.stream().map(ReducedVaticanSection::getFaithPointsEnd).sorted().toList();
        List<Integer> sectionTiles = new ArrayList<>();
        for(int i = 0; i < sectionBegins.size(); i++)
            sectionTiles.addAll(IntStream.rangeClosed(sectionBegins.get(i), sectionEnds.get(i)).boxed().toList());
        List<Integer> yellowTiles = viewModel.getFaithTrack().getYellowTiles().stream().map(ReducedYellowTile::getFaithPoints).toList();

        // Shorten nickname to fit the cell width
        List<String> players = new ArrayList<>(points.keySet().stream().toList());
        List<String> nicks = new ArrayList<>();
        for (String p : players) {
            if (p.length() > cellWidth) {
                nicks.add(p.substring(0, cellWidth));
            } else nicks.add(p);
        }

        // Indexes on top of the track
        StringBuilder output = new StringBuilder(" ");
        for(int i = 0; i <= maxFaith; i++) output.append(String.format("%-6s", Cli.centerLine(String.valueOf(i), 6)));
        output.append("\n");

        // Upper border
        printTrackHorizontalBorder(boldTopLeftCorner, slimTopLeftCorner, boldTopRightCorner, slimTopRightCorner,
                boldT, slimT, boldHorizontalLine, slimHorizontalLine, cellWidth, maxFaith, sectionTiles, yellowTiles, output);

        //Number of lines = number of players
        for (int j = 0; j < players.size(); j++) {
            String player = players.get(j);
            // Tiles in middle rows
            for (int i = 0; i <= maxFaith; i++) {
                if((yellowTiles.contains(i) && (sectionTiles.contains(i - 1))) ||
                        (sectionTiles.contains(i) && yellowTiles.contains(i - 1)))
                    output.append("\033[38;5;208m");
                else if(yellowTiles.contains(i) || yellowTiles.contains(i - 1)) output.append("\u001B[93m");
                else if(sectionTiles.contains(i) || sectionTiles.contains(i - 1)) output.append("\u001B[31m");
//                else if (i > 0 && !sectionTiles.contains(i - 1) && !yellowTiles.contains(i - 1)) output.append("\u001B[0m");
                output.append((sectionTiles.contains(i) || (i > 0 && sectionTiles.contains(i - 1))) ? boldVerticalLine : slimVerticalLine)
                        .append("\u001B[0m").append(points.get(player) == i ? String.format("%-5s", nicks.get(j)) : " ".repeat(cellWidth));
            }
            // Rightmost side border
            if(yellowTiles.contains(maxFaith)) output.append("\u001B[93m");
            else if(sectionTiles.contains(maxFaith)) output.append("\u001B[31m");
            else output.append("\u001B[0m");
            output.append(sectionTiles.contains(maxFaith) ? boldVerticalLine: slimVerticalLine).append("\u001B[0m\n");
        }

        // Lower border
        printTrackHorizontalBorder(boldBottomLeftCorner, slimBottomLeftCorner, boldBottomRightCorner,
                slimBottomRightCorner, boldUpwardsT, slimUpwardsT, boldHorizontalLine, slimHorizontalLine,
                cellWidth, maxFaith, sectionTiles, yellowTiles, output);

        try {
            // Bonus points on the bottom of the track
            output.append(" ");
            List<Integer> overlapped = new ArrayList<>();
            int index;
            for (int i = 0; i <= maxFaith; i++) {
                if (yellowTiles.contains(i) && sectionEnds.contains(i))
                    overlapped.add(i);
                if (yellowTiles.contains(i)) {
                    index = yellowTiles.indexOf(i);
                    output.append(String.format("%-15s", "\u001B[93m" + viewModel.getFaithTrack().getYellowTiles().get(index).getVictoryPoints() + " pts" + "\u001B[0m"));
                }
                else if (sectionEnds.contains(i)) {
                    output.append(String.format("%-15s", "\u001B[31m" + viewModel.getFaithTrack().getVaticanSections().get(i).getVictoryPoints() + " pts" + "\u001B[0m"));
                }
                else
                    output.append("      ");
            }
            output.append("\n");
            for (int i = 0; i <= maxFaith; i++) {
                if (overlapped.contains(i))
                    output.append(String.format("%-16s", "\u001B[31m+ " + viewModel.getFaithTrack().getVaticanSections().get(i).getVictoryPoints() + " pts" + "\u001B[0m"));
                else
                    output.append("      ");
            }
            output.append("\n");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Print the result
        System.out.println(output);

    }

    private void printTrackHorizontalBorder(String boldLeftCorner, String slimLeftCorner, String boldRightCorner,
                                            String slimRightCorner, String boldT, String slimT, String boldHorizontalLine,
                                            String slimHorizontalLine, int cellWidth, int maxFaith, List<Integer> sectionTiles,
                                            List<Integer> yellowTiles, StringBuilder output) {
        //First tile
        if(yellowTiles.contains(0) && sectionTiles.contains(0)) output.append("\033[38;5;208m");
        else if(yellowTiles.contains(0)) output.append("\u001B[93m");
        else if(sectionTiles.contains(0)) output.append("\u001B[31m");
        output.append(sectionTiles.contains(0) ? boldLeftCorner : slimLeftCorner);
        // Middle tiles
        for(int i = 0; i < maxFaith; i++){
            if(yellowTiles.contains(i)) output.append("\u001B[93m");
            else if(sectionTiles.contains(i)) output.append("\u001B[31m");
            else output.append("\u001B[0m");
            output.append((sectionTiles.contains(i) ? boldHorizontalLine : slimHorizontalLine).repeat(cellWidth));

            if((yellowTiles.contains(i + 1) && sectionTiles.contains(i)) ||
                    (sectionTiles.contains(i + 1) && yellowTiles.contains(i)))
                output.append("\033[38;5;208m");
            else if(yellowTiles.contains(i + 1) || yellowTiles.contains(i)) output.append("\u001B[93m");
            else if(sectionTiles.contains(i + 1) || sectionTiles.contains(i)) output.append("\u001B[31m");
            output.append((sectionTiles.contains(i + 1) || (i > 0 && sectionTiles.contains(i))) ? boldT : slimT);
        }
        //Last tile
        if(yellowTiles.contains(maxFaith)) output.append("\u001B[93m");
        else if(sectionTiles.contains(maxFaith)) output.append("\u001B[31m");
        else output.append("\u001B[0m");
        output.append((sectionTiles.contains(maxFaith) ? boldHorizontalLine : slimHorizontalLine).repeat(cellWidth))
                .append(sectionTiles.contains(maxFaith) ? boldRightCorner : slimRightCorner).append("\u001B[0m\n");
    }

    private void printDevelopmentSlots(Map<Integer, ReducedDevCard> slots) {
        for(int i = 0; i < slots.size(); i += 4) {
            cli.trackSlimLine();
            List<List<String>> rows = new ArrayList<>();

            List<ReducedDevCard> cards = new ArrayList<>();
            for(int j = 0; j < 4 && j < slots.size() - i; j++) {
                cards.add(slots.get(i + j));
                rows.add(new ArrayList<>());
            }

            for(int j = 0; j < 4 && j < slots.size() - i; j++) {
                List<String> column = rows.get(j);
                ReducedDevCard card = cards.get(j);

                addDevToPrinter(column, card);
            }

            String rowTemplate = "";
            for(int j = 0; j < 4 && j < slots.size() - i; j++) {
                rowTemplate += "%-38s │";
            }
            rowTemplate += "\n";

            int length = rows.stream().map(List::size).reduce(Integer::max).orElse(0);
            for(int k = 0; k < length; k++) {
                List<String> row = new ArrayList<>();
                for(int j = 0; j < 4 && j < slots.size() - i; j++) {
                    if(k < rows.get(j).size())
                        row.add(rows.get(j).get(k));
                    else row.add("");
                }
                cli.getOut().printf(rowTemplate, row.toArray());
            }
        }
        cli.trackSlimLine();
        cli.getOut().println("\n");

    }

    private void addDevToPrinter(List<String> column, ReducedDevCard card){
        if(card != null) {
            column.add("[Development]");
            column.add(String.format("%-64s", String.format("ID: \u001B[1m\u001B[37m%d\u001B[0m, color: %s",
                    card.getId(),
                    printColor(card.getColor())
            )));
            column.add(String.format("Level: %d, VP: %d",
                    card.getLevel(),
                    card.getVictoryPoints()
            ));
            addResRequirementsToPrinter(column, card.getCost());
            addProductionToPrinter(column, card.getProduction());
        }
    }

}
