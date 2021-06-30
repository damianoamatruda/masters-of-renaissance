package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.common.events.mvevents.UpdateAction;
import it.polimi.ingsw.common.events.mvevents.UpdateAction.ActionType;
import it.polimi.ingsw.common.events.mvevents.errors.ErrAction;
import it.polimi.ingsw.common.events.mvevents.errors.ErrAction.ErrActionReason;
import it.polimi.ingsw.common.events.vcevents.ReqChooseResources;
import it.polimi.ingsw.common.events.vcevents.ReqQuit;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceContainer;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceType;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static it.polimi.ingsw.client.cli.Cli.center;

public class SetupResourcesController extends SetupController {
    @Override
    public void render() {
        cli.getOut().println();
        cli.getOut().println(center("~ Choose initial resources ~"));

        Set<String> allowedResources = vm.getResourceTypes().stream()
                .map(ReducedResourceType::getName)
                .filter(r -> !vm.getLocalPlayer().flatMap(vm::getPlayerData).orElseThrow().getSetup().orElseThrow().getInitialExcludedResources().orElse(new ArrayList<>()).contains(r))
                .map(r -> vm.getResourceTypes().stream().filter(res -> res.getName().equals(r)).findAny())
                .flatMap(Optional::stream)
                .filter(ReducedResourceType::isStorable)
                .map(ReducedResourceType::getName)
                .collect(Collectors.toUnmodifiableSet());

        int totalQuantity = vm.getLocalPlayer().flatMap(vm::getPlayerData).orElseThrow().getSetup().orElseThrow().getInitialResources();

        Set<Integer> allowedShelves = vm.getLocalPlayer().map(vm::getPlayerShelves).orElseThrow().stream()
                .map(ReducedResourceContainer::getId)
                .collect(Collectors.toUnmodifiableSet());

        cli.promptShelvesSetup(allowedResources, totalQuantity, allowedShelves).ifPresentOrElse(shelves -> {
            cli.getUi().dispatch(new ReqChooseResources(shelves));
        }, () -> cli.prompt("You cannot go back. Do you want to quit to title? [y/n]").ifPresentOrElse(input -> {
            if (input.equalsIgnoreCase("y"))
                cli.getUi().dispatch(new ReqQuit());
            else
                cli.setController(this, false);
        }, () -> cli.setController(this, false)));
    }

    @Override
    public void on(ErrAction event) {
        if (event.getReason() != ErrActionReason.LATE_SETUP_ACTION)
            throw new RuntimeException("Resources setup: ErrAction received with reason not LATE_SETUP_ACTION.");
        
        super.on(event);
    }

    @Override
    public void on(UpdateAction event) {
        if (event.getAction() != ActionType.CHOOSE_RESOURCES && vm.getLocalPlayer().isPresent() && event.getPlayer().equals(vm.getLocalPlayer().get()))
            throw new RuntimeException("Resources setup: UpdateAction received with action type not CHOOSE_RESOURCES.");

        // super.on(event); // TODO: not needed, as UpdateSetupDone will take care of state switching, see SetupController
    }
}
