package it.polimi.ingsw.client.cli.components;

import it.polimi.ingsw.client.cli.Cli;
import it.polimi.ingsw.client.viewmodel.ViewModel;
import it.polimi.ingsw.common.reducedmodel.ReducedLeaderCard;

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

        stringBuilder.append(String.format("ID: \u001B[1m\u001B[37m%d\u001B[0m, type: %s",
                reducedLeaderCard.getId(),
                reducedLeaderCard.getLeaderType()
        )).append("\n");

        stringBuilder.append(String.format("Bound resource: %s, VP: %d",
                new Resource(reducedLeaderCard.getResourceType()).getString(),
                reducedLeaderCard.getVictoryPoints()
        )).append("\n");

        stringBuilder.append(String.format("Active status: %s", reducedLeaderCard.isActive())).append("\n");

        if (reducedLeaderCard.getDiscount() >= 0)
            stringBuilder.append(String.format("Discount: %d", reducedLeaderCard.getDiscount())).append("\n");

        if (reducedLeaderCard.getDevCardRequirement().isPresent())
            stringBuilder.append(new DevCardRequirement(reducedLeaderCard.getDevCardRequirement().get()).getString());
        if (reducedLeaderCard.getResourceRequirement().isPresent())
            stringBuilder.append(new ResourceRequirement(reducedLeaderCard.getResourceRequirement().get()).getString());

        vm.getContainer(reducedLeaderCard.getContainerId()).ifPresent(c ->
                stringBuilder.append(new ResourceContainer(c).getString()));

        vm.getProduction(reducedLeaderCard.getProduction()).ifPresent(p ->
                stringBuilder.append(new ResourceTransactionRecipe(p).getString()));

        return stringBuilder.toString();
    }
}
