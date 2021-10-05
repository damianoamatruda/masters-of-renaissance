package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.cli.components.ProductionSet;
import it.polimi.ingsw.client.cli.components.ResourceContainerSet;
import it.polimi.ingsw.common.events.mvevents.UpdateAction;
import it.polimi.ingsw.common.events.vcevents.ReqActivateProductions;
import it.polimi.ingsw.common.reducedmodel.ReducedProductionRequest;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceContainer;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceTransactionRecipe;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceType;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static it.polimi.ingsw.client.cli.Cli.center;

public class ActivateProductionsController extends CliController {
    private final CliController sourceController;
    private final List<ReducedProductionRequest> requests;
    private boolean done;
    private ReducedResourceTransactionRecipe selectedProd;
    private Map<String, Integer> inputReplacement;
    private Map<String, Integer> outputReplacement;
    private Map<Integer, Map<String, Integer>> shelves;

    public ActivateProductionsController(CliController sourceController) {
        this.sourceController = sourceController;
        this.requests = new ArrayList<>();
        this.done = false;
    }

    @Override
    public void render() {
        chooseProductions();
    }

    private void chooseProductions() {
        cli.getOut().println();
        cli.getOut().println(center("~ Activate Productions ~"));

        cli.getOut().println();
        new ResourceContainerSet(
                vm.getLocalPlayer().orElseThrow(),
                vm.getLocalPlayer().map(vm::getPlayerWarehouseShelves).orElseThrow(),
                vm.getLocalPlayer().map(vm::getPlayerDepots).orElseThrow(),
                vm.getLocalPlayer().flatMap(vm::getPlayerStrongbox).orElse(null))
                .render();

        List<ReducedResourceTransactionRecipe> allowedProds = vm.getLocalPlayer().map(vm::getPlayerProductions).orElseThrow();

        cli.getOut().println(center(String.format("%n%s's productions:", vm.getLocalPlayer().orElseThrow())));
        cli.getOut().println();
        cli.getOut().println(center(new ProductionSet(allowedProds).getString()));

        cli.getOut().println();

        while (!this.done) {
            AtomicBoolean valid = new AtomicBoolean(false);
            while (!valid.get()) {
                valid.set(true);
                cli.promptInt("Production").ifPresentOrElse(productionId -> allowedProds.stream().filter(p -> p.getId() == productionId).findAny().ifPresentOrElse(selectedProd -> {
                    this.selectedProd = selectedProd;
                    if (!vm.getProductionInputNonStorableResTypes(selectedProd).isEmpty())
                        chooseInputReplacements();
                    else {
                        inputReplacement = new HashMap<>();
                        chooseOutputReplacements();
                    }
                }, () -> valid.set(false)), () -> {
                    this.requests.clear();
                    this.done = true;
                });
            }
        }
        this.done = false; /* Allow Cli::reloadController to start from scratch */

        if (!this.requests.isEmpty())
            cli.getUi().dispatch(new ReqActivateProductions(this.requests));
        else
            cli.setController(this.sourceController);
    }

    private void chooseInputReplacements() {
        if (vm.getProductionInputNonStorableResTypes(selectedProd).isEmpty())
            return;

        cli.getOut().println();
        cli.getOut().println(center("-- Input replacements --"));
        cli.getOut().println();
        cli.promptResources(
                vm.getProductionInputNonStorableResTypes(selectedProd).stream().map(ReducedResourceType::getName).collect(Collectors.toUnmodifiableSet()),
                this.selectedProd.getInputBlanks()
        ).ifPresentOrElse(inputReplacement -> {
            this.inputReplacement = inputReplacement;
            chooseOutputReplacements();
        }, this::chooseProductions);
    }

    private void chooseOutputReplacements() {
        cli.getOut().println();
        cli.getOut().println(center("-- Output replacements --"));
        cli.getOut().println();
        cli.promptResources(
                vm.getProductionOutputResTypes(selectedProd).stream().map(ReducedResourceType::getName).collect(Collectors.toUnmodifiableSet()),
                this.selectedProd.getOutputBlanks()
        ).ifPresentOrElse(outputReplacement -> {
            this.outputReplacement = outputReplacement;
            chooseShelves();
        }, this::chooseInputReplacements);
    }

    private void chooseShelves() {
        Map<String, Integer> totalRes = new HashMap<>(this.selectedProd.getInput());
        this.inputReplacement.forEach((replRes, replCount) -> totalRes.compute(replRes, (res, origCount) -> origCount == null ? replCount : origCount + replCount));

        Set<Integer> allowedShelves = vm.getLocalPlayer().map(vm::getPlayerShelves).orElseThrow().stream()
                .map(ReducedResourceContainer::getId)
                .collect(Collectors.toSet());
        vm.getLocalPlayer().flatMap(vm::getPlayerStrongbox).map(ReducedResourceContainer::getId).ifPresent(allowedShelves::add);

        cli.getOut().println();
        cli.getOut().println(center("-- Containers to take resources from --"));
        cli.getOut().println();
        cli.promptShelves(totalRes, allowedShelves, true, false).ifPresentOrElse(shelves -> {
            this.shelves = shelves;
            this.requests.add(new ReducedProductionRequest(this.selectedProd.getId(), this.shelves, this.inputReplacement, this.outputReplacement));
            chooseDone();
        }, this::chooseOutputReplacements);
    }

    private void chooseDone() {
        cli.getOut().println();
        cli.prompt("Done [y/n]").ifPresentOrElse(input -> this.done = input.equalsIgnoreCase("y"), this::chooseShelves);
    }

    @Override
    public void on(UpdateAction event) {
        cli.promptPause();
        cli.setController(new TurnAfterActionController());
    }
}
