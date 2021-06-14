package it.polimi.ingsw.client.cli.components;

import it.polimi.ingsw.client.cli.Cli;
import it.polimi.ingsw.common.reducedmodel.ReducedDevCard;
import it.polimi.ingsw.common.reducedmodel.ReducedDevCardGrid;

import java.util.ArrayList;
import java.util.List;

import static it.polimi.ingsw.client.cli.Cli.center;
import static it.polimi.ingsw.client.cli.Cli.maxLinesHeight;

public class DevCardGrid extends StringComponent {
    private final static int cellWidth = 28;

    private final ReducedDevCardGrid reducedDevCardGrid;

    public DevCardGrid(ReducedDevCardGrid reducedDevCardGrid) {
        this.reducedDevCardGrid = reducedDevCardGrid;
    }

    @Override
    public String getString(Cli cli) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("Development Card Grid:").append("\n").append("\n");

        List<List<ReducedDevCard>> topCards = new ArrayList<>();
        int levels = reducedDevCardGrid.getLevelsCount();

        for (int i = 1; i <= levels; i++) {
            for (String key : reducedDevCardGrid.getTopCards().keySet()) {
                ReducedDevCard card = cli.getViewModel().getDevCardFromGrid(key, levels + 1 - i).orElseThrow();
                topCards.add(new ArrayList<>());
                topCards.get(i - 1).add(card);
            }

            int maxHeight = maxLinesHeight(topCards.get(i - 1).stream().map(c -> new DevelopmentCard(c).getString(cli)).toList());

            List<List<String>> rows = new ArrayList<>();
            for (int j = 0; j < topCards.get(i - 1).size(); j++) {
                ReducedDevCard card = topCards.get(i - 1).get(j);
                rows.add(new Box(new DevelopmentCard(card), -1, cellWidth, maxHeight).getString(cli).lines().toList());
            }

            int length = rows.stream().map(List::size).reduce(Integer::max).orElse(0);
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

        return center(stringBuilder.toString());
    }
}
