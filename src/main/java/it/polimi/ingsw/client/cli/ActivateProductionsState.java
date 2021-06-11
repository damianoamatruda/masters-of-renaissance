package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.cli.components.ResourceTransactionRecipe;
import it.polimi.ingsw.client.viewmodel.ViewModel;
import it.polimi.ingsw.common.events.mvevents.UpdateAction;
import it.polimi.ingsw.common.events.vcevents.ReqActivateProduction;
import it.polimi.ingsw.common.reducedmodel.ReducedProductionRequest;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceContainer;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceTransactionRecipe;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceType;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class ActivateProductionsState extends CliState {
    private final CliState sourceState;
    private final List<ReducedProductionRequest> requests;
    private boolean done;
    private ReducedResourceTransactionRecipe selectedProd;
    private Map<String, Integer> inputReplacement;
    private Map<String, Integer> outputReplacement;
    private Map<Integer, Map<String, Integer>> shelves;

    public ActivateProductionsState(CliState sourceState) {
        this.sourceState = sourceState;
        this.requests = new ArrayList<>();
        this.done = false;
    }

    @Override
    public void render(Cli cli) {
        chooseProductions(cli);
    }

    private void chooseProductions(Cli cli) {
        ViewModel vm = cli.getViewModel();

        List<ReducedResourceTransactionRecipe> allowedProds = vm.getPlayerProductions(vm.getLocalPlayerNickname());

        allowedProds.forEach(p -> new ResourceTransactionRecipe(p).render(cli));
        cli.getOut().println();
        cli.getOut().println("Choose which production to activate:");

        while (!this.done) {
            AtomicBoolean valid = new AtomicBoolean(false);
            while (!valid.get()) {
                valid.set(true);
                cli.promptInt("Production").ifPresentOrElse(productionId -> {
                    allowedProds.stream().filter(p -> p.getId() == productionId).findAny().ifPresentOrElse(selectedProd -> {
                        this.selectedProd = selectedProd;
                        chooseInputReplacements(cli);
                    }, () -> valid.set(false));
                }, () -> {
                    // TODO: Take only one step back
                    this.requests.clear();
                    this.done = true;
                });
            }
        }

        if (!this.requests.isEmpty())
            cli.dispatch(new ReqActivateProduction(this.requests));
        else
            cli.setState(this.sourceState);
    }

    private void chooseInputReplacements(Cli cli) {
        ViewModel vm = cli.getViewModel();

        cli.getOut().println();
        cli.getOut().println("-- Input replacements --");
        cli.getOut().println();
        cli.promptResources(
                vm.getResourceTypes().stream()
                        .filter(r -> (r.isStorable() || r.isTakeableFromPlayer()) &&
                                     !this.selectedProd.getInputBlanksExclusions().contains(r.getName()))
                        .map(ReducedResourceType::getName).collect(Collectors.toUnmodifiableSet()),
                this.selectedProd.getInputBlanks()
        ).ifPresentOrElse(inputReplacement -> {
            this.inputReplacement = inputReplacement;
            chooseOutputReplacements(cli);
        }, () -> chooseProductions(cli));
    }

    private void chooseOutputReplacements(Cli cli) {
        ViewModel vm = cli.getViewModel();

        cli.getOut().println();
        cli.getOut().println("-- Output replacements --");
        cli.getOut().println();
        cli.promptResources(
                vm.getResourceTypes().stream()
                        .filter(r -> (r.isStorable() || r.isGiveableToPlayer()) &&
                                     !this.selectedProd.getOutputBlanksExclusions().contains(r.getName()))
                        .map(ReducedResourceType::getName).collect(Collectors.toUnmodifiableSet()),
                this.selectedProd.getOutputBlanks()
        ).ifPresentOrElse(outputReplacement -> {
            this.outputReplacement = outputReplacement;
            chooseShelves(cli);
        }, () -> chooseInputReplacements(cli));
    }

    private void chooseShelves(Cli cli) {
        ViewModel vm = cli.getViewModel();

        Map<String, Integer> totalRes = new HashMap<>(this.selectedProd.getInput());
        this.inputReplacement.forEach((replRes, replCount) -> totalRes.compute(replRes, (res, origCount) -> origCount == null ? replCount : origCount + replCount));

        Set<Integer> allowedShelves = vm.getPlayerShelves(vm.getLocalPlayerNickname()).stream()
                .map(ReducedResourceContainer::getId)
                .collect(Collectors.toUnmodifiableSet());

        cli.getOut().println();
        cli.getOut().println("-- Containers to take resources from --");
        cli.getOut().println();
        cli.promptShelves(totalRes, allowedShelves).ifPresentOrElse(shelves -> {
            this.shelves = shelves;
            this.requests.add(new ReducedProductionRequest(this.selectedProd.getId(), this.inputReplacement, this.outputReplacement, this.shelves));
            chooseDone(cli);
        }, () -> chooseOutputReplacements(cli));
    }

    private void chooseDone(Cli cli) {
        cli.getOut().println();
        cli.prompt("Done [y/n]").ifPresentOrElse(input -> {
            this.done = input.equalsIgnoreCase("y");
        }, () -> chooseShelves(cli));
    }

    @Override
    public void on(Cli cli, UpdateAction event) {
        cli.getOut().println();
        cli.promptPause();
        cli.setState(new TurnAfterActionState());
    }
}
