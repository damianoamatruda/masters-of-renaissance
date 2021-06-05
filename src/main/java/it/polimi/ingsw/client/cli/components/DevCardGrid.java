package it.polimi.ingsw.client.cli.components;

import it.polimi.ingsw.client.cli.Cli;
import it.polimi.ingsw.common.reducedmodel.ReducedDevCard;
import it.polimi.ingsw.common.reducedmodel.ReducedDevCardGrid;

import java.util.*;

public class DevCardGrid extends StringComponent {
    private final static int cellWidth = 30;

    private final ReducedDevCardGrid reducedDevCardGrid;

    public DevCardGrid(ReducedDevCardGrid reducedDevCardGrid) {
        this.reducedDevCardGrid = reducedDevCardGrid;
    }

    @Override
    public String getString(Cli cli) {
        StringBuilder stringBuilder = new StringBuilder();

        List<List<ReducedDevCard>> topCards = new ArrayList<>();
        int levels = reducedDevCardGrid.getLevelsCount();

        for (int i = 1; i <= levels; i++) {
            stringBuilder.append(Cli.slimLine(cellWidth * reducedDevCardGrid.getColorsCount() + 1));

            for (String key : reducedDevCardGrid.getGrid().keySet()) {
                int index = i;
                ReducedDevCard card = reducedDevCardGrid.getGrid().get(key).stream()
                        .filter(Objects::nonNull).map(Stack::peek)
                        .map(id -> cli.getViewModel().getDevelopmentCard(id).orElse(null))
                        .filter(Objects::nonNull)
                        .filter(c -> c.getLevel() == levels + 1 - index).findAny().orElseThrow();
                topCards.add(new ArrayList<>());
                topCards.get(i - 1).add(card);
            }

            List<List<String>> rows = new ArrayList<>();
            for (int j = 0; j < topCards.get(i - 1).size(); j++) {
                ReducedDevCard card = topCards.get(i - 1).get(j);
                rows.add(Arrays.asList(new DevelopmentCard(card).getString(cli).split("\\R")));
            }

            int length = rows.stream().map(List::size).reduce(Integer::max).orElse(0);
            for (int j = 0; j < length; j++) {
                stringBuilder.append("│");
                for (int l = 0; l < topCards.get(i - 1).size(); l++) {
                    if (j < rows.get(l).size())
                        stringBuilder.append(Cli.left(rows.get(l).get(j), cellWidth - 2)).append(" │");
                    else
                        stringBuilder.append(Cli.left("", cellWidth - 2)).append(" │");
                }
                stringBuilder.append("\n");
            }
        }
        stringBuilder.append(Cli.slimLine(cellWidth * reducedDevCardGrid.getColorsCount() + 1));

        return Cli.center(stringBuilder.toString());
    }
}
