package it.polimi.ingsw.client.cli.components;

import it.polimi.ingsw.client.cli.Cli;
import it.polimi.ingsw.client.cli.Renderable;
import it.polimi.ingsw.common.reducedmodel.ReducedLeaderCard;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceTransactionRecipe;

import java.util.Optional;

public class LeaderCard implements Renderable {
    private final ReducedLeaderCard reducedLeaderCard;

    public LeaderCard(ReducedLeaderCard reducedLeaderCard) {
        this.reducedLeaderCard = reducedLeaderCard;
    }

    @Override
    public void render(Cli cli) {
        cli.getOut().printf("[Leader]%n ID: %d, type: %s%n",
                reducedLeaderCard.getId(),
                reducedLeaderCard.getLeaderType()
        );
        cli.getOut().printf("BoundResource: %s, VP: %d%n",
                reducedLeaderCard.getResourceType(),
                reducedLeaderCard.getVictoryPoints()
        );
        cli.getOut().printf("%-50s", String.format("Active status: %s", reducedLeaderCard.isActive()));

        if (reducedLeaderCard.getContainerId() > -1)
            cli.getOut().printf("%-50s", String.format("Depot ID: %d", reducedLeaderCard.getContainerId()));
        if (reducedLeaderCard.getProduction() > -1)
            cli.getOut().printf("%-50s", String.format("Production ID: %d", reducedLeaderCard.getProduction()));
        if (reducedLeaderCard.getDiscount() > -1)
            cli.getOut().printf("%-50s", String.format("Discount: %d", reducedLeaderCard.getDiscount()));

        if (reducedLeaderCard.getDevCardRequirement() != null) {
            cli.getOut().println("Requirements (development cards):");
            reducedLeaderCard.getDevCardRequirement().getEntries()
                    .forEach(e -> cli.getOut().println("Color: " + new Color(e.getColor()).getString(cli) + ", level: " + e.getLevel() + ", amount: " + e.getAmount()));
        }
        if (reducedLeaderCard.getResourceRequirement() != null) {
            cli.getOut().println("Requirements (resources):");
            reducedLeaderCard.getResourceRequirement().getRequirements().forEach((key, value) -> cli.getOut().println(new Resource(key).getString(cli) + ": " + value));
        }

        cli.getOut().println();

        Optional<ReducedResourceTransactionRecipe> prod = cli.getViewModel().getProduction(reducedLeaderCard.getProduction());
        prod.ifPresent(p -> new ResourceTransactionRecipe(p).render(cli));
    }
}
