package it.polimi.ingsw.client.cli.components;

import it.polimi.ingsw.common.reducedmodel.ReducedMarket;

import java.util.List;

import static it.polimi.ingsw.client.cli.Cli.center;

/**
 * CLI component that gives a string representation of the market.
 */
public class Market extends StringComponent {
    private final ReducedMarket reducedMarket;

    public Market(ReducedMarket reducedMarket) {
        this.reducedMarket = reducedMarket;
    }

    @Override
    public String getString() {
        StringBuilder stringBuilder = new StringBuilder();

        int width = reducedMarket.getGrid().stream().map(List::size).reduce(Integer::max).orElse(0);

        stringBuilder.append("Market:").append("\n").append("\n");

        stringBuilder.append(String.format("(Replaceable resource: %s)", new Resource(reducedMarket.getReplaceableResType()).getString())).append("\n").append("\n");

        stringBuilder.append("╔").append("═".repeat(4 + (10 + 1) * (width + 1))).append("╗").append("\n");

        stringBuilder
                .append("║").append(" ".repeat(4 + (10 + 1) * width + 1))
                .append(center(new Resource(reducedMarket.getSlide()).getString(), 10))
                .append("║").append("\n");

        stringBuilder
                .append("║").append(" ".repeat(4)).append("╔").append("═".repeat((10 + 1) * width - 1))
                .append("╦").append("═".repeat(10)).append("╝").append("\n");

        for (int i = 0; i < reducedMarket.getGrid().size(); i++) {
            List<String> r = reducedMarket.getGrid().get(i);
            stringBuilder.append("║").append(" ".repeat(4)).append("║");
            for (int j = 0; j < r.size(); j++) {
                String res = r.get(j);
                stringBuilder.append(center(new Resource(res).getString(), 10));
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
            stringBuilder.append(center("^", 10)).append(" ");
        stringBuilder.append("\n").append(" ".repeat(6));
        for (int i = 1; i <= width; i++)
            stringBuilder.append(center("" + i, 10)).append(" ");

        return center(stringBuilder.toString());
    }
}
