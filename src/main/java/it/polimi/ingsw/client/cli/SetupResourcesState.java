package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.viewmodel.ViewModel;
import it.polimi.ingsw.common.events.mvevents.UpdateCurrentPlayer;
import it.polimi.ingsw.common.events.mvevents.UpdateSetupDone;
import it.polimi.ingsw.common.events.mvevents.errors.ErrAction;
import it.polimi.ingsw.common.events.mvevents.errors.ErrAction.ErrActionReason;
import it.polimi.ingsw.common.events.mvevents.errors.ErrInitialChoice;
import it.polimi.ingsw.common.events.vcevents.ReqChooseResources;
import it.polimi.ingsw.common.events.vcevents.ReqQuit;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceContainer;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceType;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static it.polimi.ingsw.client.cli.Cli.center;

public class SetupResourcesState extends CliController {
    @Override
    public void render() {
        cli.getOut().println();
        cli.getOut().println(center("~ Choose initial resources ~"));

        ViewModel vm = cli.getViewModel();

        Set<String> allowedResources = vm.getResourceTypes().stream()
                .map(ReducedResourceType::getName)
                .filter(r -> !vm.getLocalPlayerData().orElseThrow().getSetup().orElseThrow().getInitialExcludedResources().orElse(new ArrayList<>()).contains(r))
                .map(r -> vm.getResourceTypes().stream().filter(res -> res.getName().equals(r)).findAny())
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(ReducedResourceType::isStorable)
                .map(ReducedResourceType::getName)
                .collect(Collectors.toUnmodifiableSet());

        int totalQuantity = vm.getLocalPlayerData().orElseThrow().getSetup().orElseThrow().getInitialResources();

        Set<Integer> allowedShelves = vm.getPlayerShelves(vm.getLocalPlayerNickname()).stream()
                .map(ReducedResourceContainer::getId)
                .collect(Collectors.toUnmodifiableSet());

        cli.promptShelvesSetup(allowedResources, totalQuantity, allowedShelves).ifPresentOrElse(shelves -> {
            cli.getUi().dispatch(new ReqChooseResources(shelves));
        }, () -> cli.prompt("You cannot go back. Do you want to quit to title? [y/n]").ifPresentOrElse(input -> {
            if (input.equalsIgnoreCase("y"))
                cli.getUi().dispatch(new ReqQuit());
            else
                cli.setController(this);
        }, () -> cli.setController(this)));
    }

    @Override
    public void on(ErrInitialChoice event) {
        // repeats either SetupLeadersState or SetupResourcesState
        // if it doesn't, that's really bad
        cli.reloadController(event.isLeadersChoice() ? // if the error is from the initial leaders choice
                event.getMissingLeadersCount() == 0 ?
                        "Leaders already chosen" :        // if the count is zero it means the leaders were already chosen
                        String.format("Not enough leaders chosen: %d missing.", event.getMissingLeadersCount()) :
                "Resources already chosen");          // else it's from the resources choice
    }

    @Override
    public void on(ErrAction event) {
        if (event.getReason() != ErrActionReason.LATE_SETUP_ACTION)
            throw new RuntimeException("Resources setup: ErrAction received with reason not LATE_SETUP_ACTION.");
            
        setNextState(cli);
    }

    // ErrObjectNotOwned handled in CliState
    // ErrNoSuchEntity handled in CliState
    // ErrResourceReplacement handled in CliState
    // ErrReplacedTransRecipe handled in CliState
    // ErrResourceTransfer handled in CliState

    @Override
    public void on(UpdateSetupDone event) {
        super.on(event);

        setNextState(cli);
    }

    @Override
    public void on(UpdateCurrentPlayer event) {
        super.on(event);

        setNextState(cli);
    }

    private static void setNextState(Cli cli) {
        if (cli.getViewModel().getCurrentPlayer().equals(cli.getViewModel().getLocalPlayerNickname()))
            cli.setController(new TurnBeforeActionState());
        else
            cli.setController(new WaitingAfterTurnState());
    }
}
