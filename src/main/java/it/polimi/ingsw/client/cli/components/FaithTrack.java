package it.polimi.ingsw.client.cli.components;

import it.polimi.ingsw.client.ViewModel;
import it.polimi.ingsw.client.cli.Cli;
import it.polimi.ingsw.common.reducedmodel.ReducedFaithTrack;
import it.polimi.ingsw.common.reducedmodel.ReducedVaticanSection;
import it.polimi.ingsw.common.reducedmodel.ReducedYellowTile;

import java.util.*;
import java.util.stream.IntStream;

import static it.polimi.ingsw.client.cli.Cli.center;

/** Cli component that gives a string representation of the faith track. */
public class FaithTrack extends StringComponent {
    private final ReducedFaithTrack reducedFaithTrack;
    private final Map<String, Integer> points;

    public FaithTrack(ReducedFaithTrack reducedFaithTrack, Map<String, Integer> points) {
        this.reducedFaithTrack = reducedFaithTrack;
        this.points = new HashMap<>(points);
    }

    @Override
    public String getString() {
        ViewModel vm = Cli.getInstance().getViewModel();

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
        int maxFaith = reducedFaithTrack.getMaxFaith();
        // Calculate section tiles and yellow tiles, to game the colors
        List<ReducedVaticanSection> sections = reducedFaithTrack.getVaticanSections().values().stream().toList();
        List<Integer> sectionBegins = sections.stream().map(ReducedVaticanSection::getFaithPointsBeginning).sorted().toList();
        List<Integer> sectionEnds = sections.stream().map(ReducedVaticanSection::getFaithPointsEnd).sorted().toList();
        List<Integer> sectionTiles = new ArrayList<>();
        for (int i = 0; i < sectionBegins.size(); i++)
            sectionTiles.addAll(IntStream.rangeClosed(sectionBegins.get(i), sectionEnds.get(i)).boxed().toList());
        List<Integer> yellowTiles = reducedFaithTrack.getYellowTiles().stream().map(ReducedYellowTile::getFaithPoints).toList();

        // Shorten nickname to fit the cell width
        List<String> players = new ArrayList<>(points.keySet().stream().toList());
        List<String> nicks = new ArrayList<>();
        for (String p : players) {
            if (p.length() > cellWidth) {
                nicks.add(p.substring(0, cellWidth));
            } else nicks.add(p);
        }

        // Indexes on top of the track
        StringBuilder stringBuilder = new StringBuilder(" ");
        for (int i = 0; i <= maxFaith; i++)
            stringBuilder.append(center(String.valueOf(i), 6));
        stringBuilder.append("\n");

        // Upper border
        printTrackHorizontalBorder(boldTopLeftCorner, slimTopLeftCorner, boldTopRightCorner, slimTopRightCorner,
                boldT, slimT, boldHorizontalLine, slimHorizontalLine, cellWidth, maxFaith, sectionTiles, yellowTiles, stringBuilder);

        //Number of lines = number of players
        for (int j = 0; j < players.size() + (players.size() == 1 ? 1 : 0); j++) {
            // Tiles in middle rows
            for (int i = 0; i <= maxFaith; i++) {
                if ((yellowTiles.contains(i) && (sectionTiles.contains(i - 1))) ||
                        (sectionTiles.contains(i) && yellowTiles.contains(i - 1)))
                    stringBuilder.append("\033[38;5;208m");
                else if (yellowTiles.contains(i) || yellowTiles.contains(i - 1)) stringBuilder.append("\u001B[93m");
                else if (sectionTiles.contains(i) || sectionTiles.contains(i - 1)) stringBuilder.append("\u001B[31m");
//                else if (i > 0 && !sectionTiles.contains(i - 1) && !yellowTiles.contains(i - 1)) stringBuilder.append("\u001B[0m");
                stringBuilder.append((sectionTiles.contains(i) || (i > 0 && sectionTiles.contains(i - 1))) ? boldVerticalLine : slimVerticalLine);
                if (j < players.size()) {
                    String player = players.get(j);
                    stringBuilder.append("\u001B[0m").append(points.get(player) == i ?
                            center(vm.getAnsiPlayerColor(player).orElseThrow() + nicks.get(j) + "\u001B[0m", 5) : " ".repeat(cellWidth));
                } else
                    stringBuilder.append("\u001B[0m").append(vm.getBlackPoints() == i ?
                            center("\u001B[90mBlack" + "\u001B[0m", 5) : " ".repeat(cellWidth));
            }
            // Rightmost side border
            if (yellowTiles.contains(maxFaith)) stringBuilder.append("\u001B[93m");
            else if (sectionTiles.contains(maxFaith)) stringBuilder.append("\u001B[31m");
            else stringBuilder.append("\u001B[0m");
            stringBuilder.append(sectionTiles.contains(maxFaith) ? boldVerticalLine : slimVerticalLine).append("\u001B[0m\n");
        }

        // Lower border
        printTrackHorizontalBorder(boldBottomLeftCorner, slimBottomLeftCorner, boldBottomRightCorner,
                slimBottomRightCorner, boldUpwardsT, slimUpwardsT, boldHorizontalLine, slimHorizontalLine,
                cellWidth, maxFaith, sectionTiles, yellowTiles, stringBuilder);

        // Bonus points on the bottom of the track
        stringBuilder.append(" ");
        List<Integer> overlapped = new ArrayList<>();
        int index;
        for (int i = 0; i <= maxFaith; i++) {
            if (yellowTiles.contains(i) && sectionEnds.contains(i))
                overlapped.add(i);
            if (yellowTiles.contains(i)) {
                index = yellowTiles.indexOf(i);
                stringBuilder.append(center("\u001B[93m" + reducedFaithTrack.getYellowTiles().get(index).getVictoryPoints() + " pts" + "\u001B[0m", 6));
            } else if (sectionEnds.contains(i)) {
                stringBuilder.append(center("\u001B[31m" + reducedFaithTrack.getVaticanSections().get(i).getVictoryPoints() + " pts" + "\u001B[0m", 6));
            } else
                stringBuilder.append("      ");
        }
        stringBuilder.append("\n");
        for (int i = 0; i <= maxFaith; i++) {
            if (overlapped.contains(i))
                stringBuilder.append(center("\u001B[31m+ " + reducedFaithTrack.getVaticanSections().get(i).getVictoryPoints() + " pts" + "\u001B[0m", 7));
            else
                stringBuilder.append("      ");
        }

        stringBuilder.append("\n");

        int i = 1;
        // Print activation of vatican sections
        for(ReducedVaticanSection s : sections.stream().sorted(Comparator.comparingInt(ReducedVaticanSection::getFaithPointsEnd)).toList()) {
            stringBuilder.append(String.format("Section %d: %s%n", i,
                    s.isActivated() ? (vm.getLocalPlayer().isPresent() && s.getBonusGivenPlayers().contains(vm.getLocalPlayer().get())
                            ? "earned" : "missed") : "inactive"));
            i++;
        }

        return center(stringBuilder.toString());
    }

    private void printTrackHorizontalBorder(String boldLeftCorner, String slimLeftCorner, String boldRightCorner,
                                            String slimRightCorner, String boldT, String slimT, String boldHorizontalLine,
                                            String slimHorizontalLine, int cellWidth, int maxFaith, List<Integer> sectionTiles,
                                            List<Integer> yellowTiles, StringBuilder output) {
        // First tile
        if (yellowTiles.contains(0) && sectionTiles.contains(0)) output.append("\033[38;5;208m");
        else if (yellowTiles.contains(0)) output.append("\u001B[93m");
        else if (sectionTiles.contains(0)) output.append("\u001B[31m");
        output.append(sectionTiles.contains(0) ? boldLeftCorner : slimLeftCorner);

        // Middle tiles
        for (int i = 0; i < maxFaith; i++) {
            if (yellowTiles.contains(i)) output.append("\u001B[93m");
            else if (sectionTiles.contains(i)) output.append("\u001B[31m");
            else output.append("\u001B[0m");
            output.append((sectionTiles.contains(i) ? boldHorizontalLine : slimHorizontalLine).repeat(cellWidth));

            if ((yellowTiles.contains(i + 1) && sectionTiles.contains(i)) ||
                    (sectionTiles.contains(i + 1) && yellowTiles.contains(i)))
                output.append("\033[38;5;208m");
            else if (yellowTiles.contains(i + 1) || yellowTiles.contains(i)) output.append("\u001B[93m");
            else if (sectionTiles.contains(i + 1) || sectionTiles.contains(i)) output.append("\u001B[31m");
            output.append((sectionTiles.contains(i + 1) || (i > 0 && sectionTiles.contains(i))) ? boldT : slimT);
        }

        // Last tile
        if (yellowTiles.contains(maxFaith)) output.append("\u001B[93m");
        else if (sectionTiles.contains(maxFaith)) output.append("\u001B[31m");
        else output.append("\u001B[0m");
        output.append((sectionTiles.contains(maxFaith) ? boldHorizontalLine : slimHorizontalLine).repeat(cellWidth))
                .append(sectionTiles.contains(maxFaith) ? boldRightCorner : slimRightCorner).append("\u001B[0m\n");
    }
}
