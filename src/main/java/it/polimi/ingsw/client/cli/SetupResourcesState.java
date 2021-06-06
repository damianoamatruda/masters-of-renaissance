package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.viewmodel.ViewModel;
import it.polimi.ingsw.common.events.mvevents.UpdateAction;
import it.polimi.ingsw.common.events.mvevents.UpdateAction.ActionType;
import it.polimi.ingsw.common.events.mvevents.UpdateSetupDone;
import it.polimi.ingsw.common.events.mvevents.errors.ErrAction;
import it.polimi.ingsw.common.events.mvevents.errors.ErrAction.ErrActionReason;
import it.polimi.ingsw.common.events.mvevents.errors.ErrInitialChoice;
import it.polimi.ingsw.common.events.vcevents.ReqChooseResources;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceContainer;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceType;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SetupResourcesState extends CliState {
    private final int choosable;

    public SetupResourcesState(int choosable) {
        this.choosable = choosable;
    }

    @Override
    public void render(Cli cli) {
        cli.getOut().println();
        cli.getOut().println("Choosing initial resources.");

        ViewModel vm = cli.getViewModel();

        Set<String> allowedResources = vm.getResourceTypes().stream()
                .map(ReducedResourceType::getName)
                .filter(r -> !vm.getLocalPlayerData().getSetup().getInitialExcludedResources().contains(r))
                .collect(Collectors.toUnmodifiableSet());

        int totalQuantity = vm.getLocalPlayerData().getSetup().getInitialResources();

        Set<Integer> allowedShelves = vm.getPlayerShelves(vm.getLocalPlayerNickname()).stream()
                .map(ReducedResourceContainer::getId)
                .collect(Collectors.toUnmodifiableSet());

        Map<Integer, Map<String, Integer>> shelves = cli.promptShelvesSetup(allowedResources, totalQuantity, allowedShelves);

        cli.dispatch(new ReqChooseResources(shelves));
    }

    @Override
    public void on(Cli cli, ErrInitialChoice event) {
        // repeats either SetupLeadersState or SetupResourcesState
        // if it doesn't, that's really bad
        cli.repeatState(event.isLeadersChoice() ? // if the error is from the initial leaders choice
                event.getMissingLeadersCount() == 0 ?
                        "Leaders already chosen" :        // if the count is zero it means the leaders were already chosen
                        String.format("Not enough leaders chosen: %d missing.", event.getMissingLeadersCount()) :
                "Resources already chosen");          // else it's from the resources choice
    }

    @Override
    public void on(Cli cli, ErrAction event) {
        if (event.getReason() != ErrActionReason.LATE_SETUP_ACTION)
            throw new RuntimeException("Resources setup: ErrAction received with reason not LATE_SETUP_ACTION.");

        if (cli.getViewModel().getCurrentPlayer().equals(cli.getViewModel().getLocalPlayerNickname()))
            cli.setState(new TurnBeforeActionState());
        else
            cli.setState(new WaitingAfterTurnState());
    }

    // ErrObjectNotOwned handled in CliState
    // ErrNoSuchEntity handled in CliState
    // ErrResourceReplacement handled in CliState
    // ErrReplacedTransRecipe handled in CliState
    // ErrInitialChoice handled in CliState
    // ErrResourceTransfer handled in CliState

    @Override
    public void on(Cli cli, UpdateSetupDone event) {
        super.on(cli, event);

        if (cli.getViewModel().getCurrentPlayer().equals(cli.getViewModel().getLocalPlayerNickname()))
            cli.setState(new TurnBeforeActionState());
        else
            cli.setState(new WaitingAfterTurnState());
    }
}
