package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.cli.components.LeadersHand;
import it.polimi.ingsw.client.cli.components.Market;
import it.polimi.ingsw.client.viewmodel.ViewModel;
import it.polimi.ingsw.common.backend.model.leadercards.ZeroLeader;
import it.polimi.ingsw.common.events.mvevents.UpdateAction;
import it.polimi.ingsw.common.events.vcevents.ReqTakeFromMarket;
import it.polimi.ingsw.common.reducedmodel.ReducedLeaderCard;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TakeFromMarketState extends CliState {

    @Override
    public void render(Cli cli) {
        ViewModel vm = cli.getViewModel();


        // print market and shelves
        new Market(vm.getMarket()).render(cli);
        cli.showWarehouseShelves(vm.getLocalPlayerNickname());

        cli.getOut().println("Getting resources from the market:");

        boolean isValid = false;
        boolean isRow = false;
        int index = -1;
        String input;

        // get user input, parse row/col and index
        while (!isValid) {
            isValid = true;
            input = cli.prompt("[row/col]");
            
            if(input.equalsIgnoreCase("row"))
                isRow = true;
            else if(!(input.equalsIgnoreCase("col"))) {
                isValid = false;
            }
        }
        isValid = false;

        while (!isValid) {
            input = cli.prompt("Index (1-based)");
            
            try {
                index = Integer.parseInt(input) - 1;

                if (index >= 0)
                    isValid = true;
            } catch (Exception e) {
                cli.getOut().println("Please input an integer.");
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
                        blanksCount,
                        zeroLeaders.stream().map(l -> l.getResourceType().getName()).toList());
                }
            }
        }

        // replacements = replacements.entrySet().stream().filter(e -> e.getKey().equals(cli.getViewModel().getMarket().getReplaceableResType())).collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));

        // remove the replaceable resources from the selected ones
        chosenResourcesNames = chosenResourcesNames.stream().filter(r -> !r.equals(vm.getMarket().getReplaceableResType())).toList();

        Map<String, Integer> totalRes = new HashMap<>(replacements);
        chosenResourcesNames.forEach(r -> totalRes.compute(r, (res, c) -> c == null ? 1 : c + 1));

        List<Integer> allowedShelvesIDs = vm.getPlayerShelves(vm.getLocalPlayerNickname()).stream().map(c -> c.getId()).toList();
        cli.dispatch(new ReqTakeFromMarket(isRow, index, replacements, cli.promptShelves(totalRes, allowedShelvesIDs)));
    }

    // ErrObjectNotOwned handled in clistate
    // ErrNoSuchEntity handled in clistate
    // ErrResourceReplacement handled in clistate
    // ErrReplacedTransRecipe handled in clistate
    // ErrResourceTransfer handled in clistate

    @Override
    public void on(Cli cli, UpdateAction event) {
        cli.setState(new TurnAfterActionState());
    }
}
