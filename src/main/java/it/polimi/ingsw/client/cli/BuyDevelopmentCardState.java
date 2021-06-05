package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.cli.components.DevCardGrid;
import it.polimi.ingsw.client.viewmodel.ViewModel;
import it.polimi.ingsw.common.events.mvevents.UpdateAction;
import it.polimi.ingsw.common.events.vcevents.ReqBuyDevCard;
import it.polimi.ingsw.common.reducedmodel.ReducedDevCard;
import it.polimi.ingsw.common.reducedmodel.ReducedDevCardGrid;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceContainer;

import java.util.*;
import java.util.stream.Collectors;

public class BuyDevelopmentCardState extends CliState {

    @Override
    public void render(Cli cli) {
        cli.getOut().println("Buying a development card.");

        ViewModel vm = cli.getViewModel();

        ReducedDevCardGrid grid = vm.getDevCardGrid();

        new DevCardGrid(grid).render(cli);
        cli.getOut().println();
        cli.showShelves(vm.getLocalPlayerNickname());
        cli.getOut().println();
        cli.showStrongbox(vm.getLocalPlayerNickname());

        String color = cli.prompt("Card color");
        int level = cli.promptInt("Card level");
        int slot = cli.promptInt("Player board slot to assign to the card");

        ReducedDevCard card = grid.getGrid().get(color).stream()
                .filter(Objects::nonNull).map(Stack::peek)
                .map(id -> vm.getDevelopmentCard(id).orElse(null))
                .filter(Objects::nonNull)
                .filter(c -> c.getLevel() == level).findAny().orElseThrow();
        Map<String, Integer> cost = new HashMap<>(card.getCost().getRequirements());

        cli.getOut().println("Resources need to be paid.");
        cli.getOut().println("Please specify how many resources to take from which container.");

        cli.getOut().println();
        cli.showShelves(vm.getLocalPlayerNickname());
        cli.getOut().println();
        cli.showStrongbox(vm.getLocalPlayerNickname());

        Set<Integer> allowedShelves = vm.getPlayerShelves(vm.getLocalPlayerNickname()).stream()
                .map(ReducedResourceContainer::getId)
                .collect(Collectors.toUnmodifiableSet());

        Map<Integer, Map<String, Integer>> shelves = cli.promptShelves(cost, allowedShelves);

        cli.dispatch(new ReqBuyDevCard(color, level, slot, shelves));
    }

    @Override
    public void on(Cli cli, UpdateAction event) {
        cli.getOut().println();
        cli.promptPause();
        cli.setState(new TurnAfterActionState());
    }
}
