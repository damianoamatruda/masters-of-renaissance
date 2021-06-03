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
                    new Color(reducedDevCard.getColor()).getString(cli)
            )));
            column.add(String.format("Level: %d, VP: %d",
                    reducedDevCard.getLevel(),
                    reducedDevCard.getVictoryPoints()
            ));
            column.addAll(Arrays.asList(new ResourceRequirement(reducedDevCard.getCost()).getString(cli).split("\n")));
            cli.getViewModel().getProduction(reducedDevCard.getProduction()).ifPresent(p ->
                    column.addAll(Arrays.asList(new ResourceTransactionRecipe(p).getString(cli).split("\n"))));
        }

        return String.join("\n", column);
    }
}
