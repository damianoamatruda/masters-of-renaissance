package it.polimi.ingsw.client.cli.components;

import it.polimi.ingsw.client.cli.Cli;
import it.polimi.ingsw.common.reducedmodel.ReducedDevCard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DevelopmentCard extends StringComponent {
    private final ReducedDevCard reducedDevCard;

    public DevelopmentCard(ReducedDevCard reducedDevCard) {
        this.reducedDevCard = reducedDevCard;
    }

    @Override
    public String getString(Cli cli) {
        List<String> column = new ArrayList<>();

        if (reducedDevCard != null) {
            column.add("[Development]");
            column.add(String.format("%-64s", String.format("ID: \u001B[1m\u001B[37m%d\u001B[0m, color: %s",
                    reducedDevCard.getId(),
                    new DevCardColor(reducedDevCard.getColor()).getString(cli)
            )));
            column.add(String.format("Level: %d, VP: %d",
                    reducedDevCard.getLevel(),
                    reducedDevCard.getVictoryPoints()
            ));
            List<String> s = Arrays.stream(new ResourceRequirement(reducedDevCard.getCost()).getString(cli).split("\\R"))
                    .filter(st -> !st.contains("\r") && !st.startsWith("\n"))
                    .map(st -> st.replaceFirst(" ".repeat(17), ""))
                    .map(st -> st.length() == 34 ? st + " ".repeat(17) : st)
                    .toList();
            column.addAll(s);
            cli.getViewModel().getProduction(reducedDevCard.getProduction()).ifPresent(p ->
                    column.addAll(Arrays.asList(new ResourceTransactionRecipe(p).getString(cli).split("\\R"))));
        }

        return String.join("\n", column);
    }
}
