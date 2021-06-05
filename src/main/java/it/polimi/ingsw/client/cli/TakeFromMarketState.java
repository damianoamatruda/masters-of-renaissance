package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.cli.components.LeadersHand;
import it.polimi.ingsw.client.cli.components.Market;
import it.polimi.ingsw.client.viewmodel.ViewModel;
import it.polimi.ingsw.common.backend.model.leadercards.ZeroLeader;
import it.polimi.ingsw.common.events.mvevents.UpdateAction;
import it.polimi.ingsw.common.events.vcevents.ReqTakeFromMarket;
import it.polimi.ingsw.common.reducedmodel.ReducedLeaderCard;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceContainer;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceType;

import java.util.*;
import java.util.stream.Collectors;

public class TakeFromMarketState extends CliState {

    @Override
    public void render(Cli cli) {
        ViewModel vm = cli.getViewModel();

        new Market(vm.getMarket()).render(cli);
        cli.getOut().println();
        cli.showShelves(vm.getLocalPlayerNickname());

        cli.getOut().println();
        cli.getOut().println("Getting resources from the market:");

        boolean isValid = false;
        boolean isRow = false;
        int index = -1;
        String input;

        while (!isValid) {
            isValid = true;

            input = cli.prompt("[row/col]");
            
            if (input.equalsIgnoreCase("row"))
                isRow = true;
            else if (!input.equalsIgnoreCase("col"))
                isValid = false;
        }
        isValid = false;

        while (!isValid) {
            try {
                index = cli.promptInt("Number") - 1;
                if (index >= 0
                        && (isRow && index < cli.getViewModel().getMarket().getGrid().size()
                        || !isRow && cli.getViewModel().getMarket().getGrid().size() > 0 && index < cli.getViewModel().getMarket().getGrid().get(0).size()))
                    isValid = true;
            } catch (Exception e) {
                cli.getOut().println("Please input an integer greater than 0.");
            }
        }

        // get a list with the selected resources
        List<ReducedResourceType> chosenResources = new ArrayList<>();
        if (isRow)
            chosenResources = vm.getMarket().getGrid().get(index);
        else {
            for (List<ReducedResourceType> row : vm.getMarket().getGrid())
                chosenResources.add(row.get(index));
        }
        List<String> chosenResourcesNames = chosenResources.stream().filter(ReducedResourceType::isStorable).map(ReducedResourceType::getName).toList();

        // if there's > 0 replaceable, get the active zeroleaders and prompt for replacements
        int blanksCount = (int) chosenResourcesNames.stream().filter(r -> r.equals(vm.getMarket().getReplaceableResType())).count();
        Map<String, Integer> replacements = new HashMap<>();

        // for (String res : chosenResources)
        //     replacements.compute(res, (k, v) -> v == null ? 1 : v++);

        if (blanksCount > 0) {
            List<ReducedLeaderCard> zeroLeaders = vm.getPlayerLeaderCards(vm.getLocalPlayerNickname()).stream()
                .filter(c -> c.isActive() &&
                        c.getLeaderType().equals(ZeroLeader.class.getSimpleName())).toList();
            
            if (zeroLeaders.size() > 0) {
                new LeadersHand(zeroLeaders).render(cli);
                cli.getOut().println("These are the active leaders you can use to replace blank resources.");
                

                input = "";
                while (!input.equals("y") && !input.equals("n"))
                    input = cli.prompt("Replace resources? [y/n]");
                if (input.equals("n")) {
                    replacements = cli.promptResources(
                            zeroLeaders.stream().map(l -> l.getResourceType().getName()).collect(Collectors.toUnmodifiableSet()), blanksCount
                    );
                }
            }
        }

        // replacements = replacements.entrySet().stream().filter(e -> e.getKey().equals(cli.getViewModel().getMarket().getReplaceableResType())).collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));

        // remove the replaceable resources from the selected ones
        chosenResourcesNames = chosenResourcesNames.stream().filter(r -> !r.equals(vm.getMarket().getReplaceableResType())).toList();

        Map<String, Integer> totalRes = new HashMap<>(replacements);
        chosenResourcesNames.forEach(r -> totalRes.compute(r, (res, c) -> c == null ? 1 : c + 1));

        Set<Integer> allowedShelves = vm.getPlayerShelves(vm.getLocalPlayerNickname()).stream()
                .map(ReducedResourceContainer::getId)
                .collect(Collectors.toUnmodifiableSet());

        cli.getOut().println();
        Map<Integer, Map<String, Integer>> shelves = cli.promptShelves(totalRes, allowedShelves);

        cli.dispatch(new ReqTakeFromMarket(isRow, index, replacements, shelves));
    }

    // ErrObjectNotOwned handled in clistate
    // ErrNoSuchEntity handled in clistate
    // ErrResourceReplacement handled in clistate
    // ErrReplacedTransRecipe handled in clistate
    // ErrResourceTransfer handled in clistate

    @Override
    public void on(Cli cli, UpdateAction event) {
        cli.getOut().println();
        cli.promptPause();
        cli.setState(new TurnAfterActionState());
    }
}
