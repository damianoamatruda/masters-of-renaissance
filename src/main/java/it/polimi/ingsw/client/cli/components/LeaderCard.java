package it.polimi.ingsw.client.cli.components;

import it.polimi.ingsw.client.cli.Cli;
import it.polimi.ingsw.common.reducedmodel.ReducedLeaderCard;

import java.util.ArrayList;
import java.util.List;

public class LeaderCard extends StringComponent {
    private final ReducedLeaderCard reducedLeaderCard;

    public LeaderCard(ReducedLeaderCard reducedLeaderCard) {
        this.reducedLeaderCard = reducedLeaderCard;
    }

    @Override
    public String getString(Cli cli) {
        List<String> column = new ArrayList<>();

        column.add(String.format("%-38s", "[Leader]"));

        column.add(String.format("%-51s", String.format("ID: \u001B[1m\u001B[37m%d\u001B[0m, type: %s",
                reducedLeaderCard.getId(),
                reducedLeaderCard.getLeaderType()
        )));

        column.add(String.format("%-51s", String.format("BoundResource: %s, VP: %d",
                new Resource(reducedLeaderCard.getResourceType().getName()).getString(cli),
                reducedLeaderCard.getVictoryPoints())
        ));

        column.add(String.format("%-38s", String.format("Active status: %s", reducedLeaderCard.isActive())));

        if (reducedLeaderCard.getDiscount() >= 0)
            column.add(String.format("%-38s", String.format("Discount: %d", reducedLeaderCard.getDiscount())));

        if (reducedLeaderCard.getDevCardRequirement() != null)
            column.add(new CardRequirements(reducedLeaderCard.getDevCardRequirement()).getString(cli));
        if (reducedLeaderCard.getResourceRequirement() != null)
            column.add(new ResRequirements(reducedLeaderCard.getResourceRequirement()).getString(cli));

        cli.getViewModel().getContainer(reducedLeaderCard.getContainerId()).ifPresent(c ->
                column.add(String.format("%-38s", new ResourceContainer(c).getString(cli))));

        cli.getViewModel().getProduction(reducedLeaderCard.getProduction()).ifPresent(p ->
                column.add(String.format("%-38s", new ResourceTransactionRecipe(p).getString(cli))));

        return String.join("\n", column) + "\n";
    }
}
