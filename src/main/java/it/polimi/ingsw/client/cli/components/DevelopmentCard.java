package it.polimi.ingsw.client.cli.components;

import it.polimi.ingsw.client.cli.Cli;
import it.polimi.ingsw.common.reducedmodel.ReducedDevCard;

import static it.polimi.ingsw.client.cli.Cli.boldColor;

/** Cli component that gives a string representation of a development card. */
public class DevelopmentCard extends StringComponent {
    private final ReducedDevCard reducedDevCard;

    public DevelopmentCard(ReducedDevCard reducedDevCard) {
        this.reducedDevCard = reducedDevCard;
    }

    @Override
    public String getString() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("[Development]").append("\n");
        stringBuilder.append(String.format("ID: %s, color: %s",
                boldColor(String.valueOf(reducedDevCard.getId()), "\u001B[37m"),
                new DevCardColor(reducedDevCard.getColor()).getString()
        )).append("\n");
        stringBuilder.append(String.format("Level: %d, VP: %d",
                reducedDevCard.getLevel(),
                reducedDevCard.getVictoryPoints()
        )).append("\n");
        reducedDevCard.getCost().ifPresent(cost -> stringBuilder.append(new ResourceRequirement(cost, true).getString()));
        Cli.getInstance().getViewModel().getProduction(reducedDevCard.getProduction()).ifPresent(p ->
                stringBuilder.append(new ResourceTransactionRecipe(p).getString()));

        return stringBuilder.toString();
    }
}
