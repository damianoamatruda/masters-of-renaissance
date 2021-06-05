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
import java.util.stream.Collectors;

public class ActivateProductionsState extends CliState {

    @Override
    public void render(Cli cli) {
        ViewModel vm = cli.getViewModel();

        List<ReducedResourceTransactionRecipe> allowedProds = vm.getPlayerProductions(vm.getLocalPlayerNickname());

        allowedProds.forEach(p -> new ResourceTransactionRecipe(p).render(cli));
        cli.getOut().println();
        cli.getOut().println("Choose which production to activate:");

        List<ReducedProductionRequest> requests = new ArrayList<>();
        String input;
        do {
            Optional<ReducedResourceTransactionRecipe> optionalSelectedProd;
            do {
                int productionId = cli.promptInt("Production ID");
                optionalSelectedProd = allowedProds.stream().filter(p -> p.getId() == productionId).findAny();
            } while (optionalSelectedProd.isEmpty());

            ReducedResourceTransactionRecipe selectedProd = optionalSelectedProd.get();

            cli.getOut().println();
            cli.getOut().println("-- Input replacements --");
            Map<String, Integer> inputReplacement = cli.promptResources(
                    vm.getResourceTypes().stream().filter(r -> (r.isStorable() || r.isTakeableFromPlayer()) && !selectedProd.getInputBlanksExclusions().contains(r.getName())).map(ReducedResourceType::getName).collect(Collectors.toUnmodifiableSet()),
                    selectedProd.getInputBlanks()
            );

            cli.getOut().println();
            cli.getOut().println("-- Output replacements --");

            cli.getOut().println();
            Map<String, Integer> outputReplacement = cli.promptResources(
                    vm.getResourceTypes().stream().filter(r -> (r.isStorable() || r.isGiveableToPlayer()) && !selectedProd.getOutputBlanksExclusions().contains(r.getName())).map(ReducedResourceType::getName).collect(Collectors.toUnmodifiableSet()),
                    selectedProd.getOutputBlanks()
            );

            Map<String, Integer> totalRes = new HashMap<>(selectedProd.getInput());
            inputReplacement.forEach((replRes, replCount) -> totalRes.compute(replRes, (res, origCount) -> origCount == null ? replCount : origCount + replCount));

            cli.getOut().println();
            cli.getOut().println("-- Containers to take resources from --");

            Set<Integer> allowedShelves = vm.getPlayerShelves(vm.getLocalPlayerNickname()).stream()
                    .map(ReducedResourceContainer::getId)
                    .collect(Collectors.toUnmodifiableSet());

            cli.getOut().println();
            Map<Integer, Map<String, Integer>> shelves = cli.promptShelves(totalRes, allowedShelves);

            requests.add(new ReducedProductionRequest(selectedProd.getId(), inputReplacement, outputReplacement, shelves));

            cli.getOut().println();
            input = cli.prompt("Are you done? [y/n]");
        } while (!input.equalsIgnoreCase("y"));

        cli.dispatch(new ReqActivateProduction(requests));
    }

    @Override
    public void on(Cli cli, UpdateAction event) {
        cli.setState(new TurnAfterActionState());
    }
}
