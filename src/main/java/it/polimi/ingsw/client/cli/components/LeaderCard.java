package it.polimi.ingsw.client.cli.components;

import it.polimi.ingsw.client.ViewModel;
import it.polimi.ingsw.client.cli.Cli;
import it.polimi.ingsw.common.reducedmodel.ReducedLeaderCard;

import static it.polimi.ingsw.client.cli.Cli.boldColor;

/**
 * CLI component that gives a string representation of a leader card.
 */
public class LeaderCard extends StringComponent {
    private final ReducedLeaderCard reducedLeaderCard;

    public LeaderCard(ReducedLeaderCard reducedLeaderCard) {
        this.reducedLeaderCard = reducedLeaderCard;
    }

    @Override
    public String getString() {
        ViewModel vm = Cli.getInstance().getViewModel();

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("[Leader]").append("\n");

        /* ID, type */
        stringBuilder.append(String.format("ID: %s, type: %s",
                boldColor(String.valueOf(reducedLeaderCard.getId()), "\u001B[37m"),
                reducedLeaderCard.getLeaderType()
        )).append("\n");

        /* Bound resource, victory points */
        stringBuilder.append(String.format("Bound resource: %s, VP: %d",
                new Resource(reducedLeaderCard.getResourceType()).getString(),
                reducedLeaderCard.getVictoryPoints()
        )).append("\n");

        /* Whether activated or not */
        stringBuilder.append(String.format("Active status: %s", reducedLeaderCard.isActive())).append("\n");

        /* Discount leaders only */
        if (reducedLeaderCard.getDiscount() >= 0)
            stringBuilder.append(String.format("Discount: %d", reducedLeaderCard.getDiscount())).append("\n");

        /* Requirements */
        if (reducedLeaderCard.getDevCardRequirement().isPresent())
            stringBuilder.append(new DevCardRequirement(reducedLeaderCard.getDevCardRequirement().get()).getString());
        if (reducedLeaderCard.getResourceRequirement().isPresent())
            stringBuilder.append(new ResourceRequirement(reducedLeaderCard.getResourceRequirement().get(), false).getString());

        /* Leader depots */
        vm.getContainer(reducedLeaderCard.getContainerId()).ifPresent(c ->
                stringBuilder.append(new ResourceContainer(c).getString()));

        /* Production */
        vm.getProduction(reducedLeaderCard.getProduction()).ifPresent(p ->
                stringBuilder.append(new ResourceTransactionRecipe(p).getString()));

        return stringBuilder.toString();
    }
}
