package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.cli.components.ResourceTransactionRecipe;
import it.polimi.ingsw.client.viewmodel.ViewModel;
import it.polimi.ingsw.common.events.mvevents.UpdateAction;
import it.polimi.ingsw.common.events.vcevents.ReqActivateProduction;
import it.polimi.ingsw.common.reducedmodel.ReducedProductionRequest;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceContainer;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceTransactionRecipe;

import java.util.*;
import java.util.stream.Collectors;

public class ActivateProductionsState extends CliState {

    @Override
    public void render(Cli cli) {
        ViewModel vm = cli.getViewModel();

        List<ReducedResourceTransactionRecipe> allowedProds = vm.getPlayerProductions(vm.getLocalPlayerNickname());

        allowedProds.forEach(p -> new ResourceTransactionRecipe(p).render(cli));
        cli.getOut().println("These are the available productions. Choose which ones to activate:");

        List<ReducedProductionRequest> requests = new ArrayList<>();
        int productionid = -1;
        String input = "";
        while (!input.equalsIgnoreCase("y")) {
            while (!allowedProds.stream().map(p -> p.getId()).toList().contains(productionid)) {
                input = cli.prompt("Production ID");
                try {
                    productionid = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                }
            }

            int pid = productionid;
            ReducedResourceTransactionRecipe selectedProd = allowedProds.stream().filter(p -> p.getId() == pid).findAny().get();

            cli.getOut().println("-- Input replacement --");
            Map<String, Integer> inputReplacement = cli.promptResources(
                selectedProd.getInputBlanks(),
                vm.getResourceTypes().stream().map(rt -> rt.getName()).toList()
            );

            cli.getOut().println("-- Output replacement --");
            Map<String, Integer> outputReplacement = cli.promptResources(
                selectedProd.getOutputBlanks(),
                vm.getResourceTypes().stream().map(rt -> rt.getName()).toList()
            );

            Map<String, Integer> totalRes = new HashMap<>(selectedProd.getInput());
            inputReplacement.forEach((replRes, replCount) -> totalRes.compute(replRes, (res, origCount) -> origCount == null ? replCount : origCount + replCount));

            cli.getOut().println("-- Containers to take resources from --");
            Set<Integer> allowedShelvesIDs = vm.getPlayerShelves(vm.getLocalPlayerNickname()).stream().map(ReducedResourceContainer::getId).collect(Collectors.toUnmodifiableSet());
            Map<Integer, Map<String, Integer>> shelves = cli.promptShelves(totalRes, allowedShelvesIDs);

            requests.add(new ReducedProductionRequest(productionid, inputReplacement, outputReplacement, shelves));
            
            input = cli.prompt("Are you done? [y/n]");
        }

        cli.dispatch(new ReqActivateProduction(requests));
    }

    @Override
    public void on(Cli cli, UpdateAction event) {
        cli.setState(new TurnAfterActionState());
    }
}
