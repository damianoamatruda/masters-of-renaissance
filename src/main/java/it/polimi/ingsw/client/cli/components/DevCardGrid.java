package it.polimi.ingsw.client.cli.components;

import it.polimi.ingsw.client.cli.Cli;
import it.polimi.ingsw.common.reducedmodel.ReducedDevCard;
import it.polimi.ingsw.common.reducedmodel.ReducedDevCardGrid;

import java.util.ArrayList;
import java.util.List;

import static it.polimi.ingsw.client.cli.Cli.center;
import static it.polimi.ingsw.client.cli.Cli.maxLinesHeight;

/** Cli component that gives a string representation of the development card grid. */
public class DevCardGrid extends StringComponent {
    private final static int cellWidth = 28;

    private final ReducedDevCardGrid reducedDevCardGrid;

    public DevCardGrid(ReducedDevCardGrid reducedDevCardGrid) {
        this.reducedDevCardGrid = reducedDevCardGrid;
    }

    @Override
    public String getString() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("Development Card Grid:").append("\n").append("\n");

        List<List<ReducedDevCard>> topCards = new ArrayList<>();
        int levels = reducedDevCardGrid.getLevelsCount();

        for (int i = 1; i <= levels; i++) {
            fetchReducedGridByLevel(topCards, i);

            int maxHeight = maxLinesHeight(topCards.get(i - 1).stream().map(c -> new DevelopmentCard(c).getString()).toList());

            List<List<String>> rows = new ArrayList<>();
            fillRows(rows, topCards, i, maxHeight);

            int length = rows.stream().map(List::size).reduce(Integer::max).orElse(0);
            buildCliCardRow(stringBuilder, topCards, length, i, rows);
        }

        return center(stringBuilder.toString());
    }

    /**
     * Adds the built string component to the stringBuilder
     *
     * @param stringBuilder the stringBuilder
     * @param topCards      the top reduced cards of the grid
     * @param length        the max number of lines to be printed for this row
     * @param i             the level of the cards now being represented
     * @param rows          the lines representing the current row of cards
     */
    private void buildCliCardRow(StringBuilder stringBuilder, List<List<ReducedDevCard>> topCards, int length, int i, List<List<String>> rows) {
        for (int j = 0; j < length; j++) {
            for (int l = 0; l < topCards.get(i - 1).size(); l++) {
                if (j < rows.get(l).size())
                    stringBuilder.append(rows.get(l).get(j)).append(" ");
                else
                    stringBuilder.append("".repeat(cellWidth + 1));
            }
            stringBuilder.append("\n");
        }
    }

    /**
     * Fills the list that will contain the lines to be printed.
     *
     * @param rows      the list of strings
     * @param topCards  the cards of the grid
     * @param i         the level of the topCards to be represented
     * @param maxHeight the height of the cards boxing
     */
    private void fillRows(List<List<String>> rows, List<List<ReducedDevCard>> topCards, int i, int maxHeight) {
        for (int j = 0; j < topCards.get(i - 1).size(); j++) {
            ReducedDevCard card = topCards.get(i - 1).get(j);
            rows.add(new Box(new DevelopmentCard(card), cellWidth, maxHeight, -1).getString().lines().toList());
        }
    }

    /**
     * Fills a list of reduced cards available in the grid of a certain level.
     *
     * @param topCards the fillable list of reduced cards
     * @param i        the card level
     */
    private void fetchReducedGridByLevel(List<List<ReducedDevCard>> topCards, int i) {
        int levels = reducedDevCardGrid.getLevelsCount();

        for (String key : reducedDevCardGrid.getTopCards().keySet()) {
            ReducedDevCard card = Cli.getInstance().getViewModel().getDevCardFromGrid(key, levels + 1 - i).orElseThrow();
            topCards.add(new ArrayList<>());
            topCards.get(i - 1).add(card);
        }
    }
}
