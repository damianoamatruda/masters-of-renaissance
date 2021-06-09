package it.polimi.ingsw.client.cli.components;

import it.polimi.ingsw.client.cli.Cli;
import it.polimi.ingsw.common.reducedmodel.ReducedMarket;

import java.util.List;

public class Market extends StringComponent {
    private final ReducedMarket reducedMarket;

    public Market(ReducedMarket reducedMarket) {
        this.reducedMarket = reducedMarket;
    }

    @Override
    public String getString(Cli cli) {
        StringBuilder stringBuilder = new StringBuilder();

        int width = reducedMarket.getGrid().stream().map(List::size).reduce(Integer::max).orElse(0);

        stringBuilder.append("Market. Replaceable resource type: ").append(new Resource(reducedMarket.getReplaceableResType()).getString(cli)).append("\n").append("\n");

        stringBuilder.append("╔").append("═".repeat(4 + (10 + 1) * (width + 1))).append("╗").append("\n");

        stringBuilder
                .append("║").append(" ".repeat(4 + (10 + 1) * width + 1))
                .append(Cli.centerLine(new Resource(reducedMarket.getSlide()).getString(cli), 10))
                .append("║").append("\n");

        stringBuilder
                .append("║").append(" ".repeat(4)).append("╔").append("═".repeat((10 + 1) * width - 1))
                .append("╦").append("═".repeat(10)).append("╝").append("\n");

        for (int i = 0; i < reducedMarket.getGrid().size(); i++) {
            List<String> r = reducedMarket.getGrid().get(i);
            stringBuilder.append("║").append(" ".repeat(4)).append("║");
            for (int j = 0; j < r.size(); j++) {
                String res = r.get(j);
                stringBuilder.append(Cli.centerLine(new Resource(res).getString(cli), 10));
                if (j < r.size() - 1)
                    stringBuilder.append("│");
            }
            stringBuilder.append(String.format("║ < %d", i + 1)).append("\n");
            if (i < reducedMarket.getGrid().size() - 1) {
                stringBuilder.append("║").append(" ".repeat(4)).append("║");
                stringBuilder.append(("─".repeat(10) + "┼").repeat(r.size() - 1)).append("─".repeat(10));
                stringBuilder.append("║").append("\n");
            }
        }

        stringBuilder.append("╚").append("═".repeat(4)).append("╩").append("═".repeat((10 + 1) * width - 1)).append("╝").append("\n");
        stringBuilder.append(" ".repeat(6));
        for (int i = 1; i <= width; i++)
            stringBuilder.append(Cli.centerLine("^", 10)).append(" ");
        stringBuilder.append("\n").append(" ".repeat(6));
        for (int i = 1; i <= width; i++)
            stringBuilder.append(Cli.centerLine("" + i, 10)).append(" ");

        return Cli.center(stringBuilder.toString());
    }
}
