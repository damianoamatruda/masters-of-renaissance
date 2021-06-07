package it.polimi.ingsw.client.cli.components;

import it.polimi.ingsw.client.cli.Cli;
import it.polimi.ingsw.common.reducedmodel.ReducedDevCard;

public class DevelopmentCard extends StringComponent {
    private final ReducedDevCard reducedDevCard;

    public DevelopmentCard(ReducedDevCard reducedDevCard) {
        this.reducedDevCard = reducedDevCard;
    }

    @Override
    public String getString(Cli cli) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("[Development]").append("\n");
        stringBuilder.append(String.format("ID: \u001B[1m\u001B[37m%d\u001B[0m, color: %s",
                reducedDevCard.getId(),
                new DevCardColor(reducedDevCard.getColor()).getString(cli)
        )).append("\n");
        stringBuilder.append(String.format("Level: %d, VP: %d",
                reducedDevCard.getLevel(),
                reducedDevCard.getVictoryPoints()
        )).append("\n");
        reducedDevCard.getCost().ifPresent(cost -> stringBuilder.append(new ResourceRequirement(cost).getString(cli)));
        cli.getViewModel().getProduction(reducedDevCard.getProduction()).ifPresent(p ->
                stringBuilder.append(new ResourceTransactionRecipe(p).getString(cli)));

        return stringBuilder.toString();
    }
}
