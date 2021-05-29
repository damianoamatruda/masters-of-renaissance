package it.polimi.ingsw.client.cli;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import it.polimi.ingsw.client.ViewModel.ViewModel;
import it.polimi.ingsw.common.backend.model.leadercards.ZeroLeader;
import it.polimi.ingsw.common.events.mvevents.UpdateAction;
import it.polimi.ingsw.common.events.mvevents.UpdateMarket;
import it.polimi.ingsw.common.events.mvevents.UpdateResourceContainer;
import it.polimi.ingsw.common.events.vcevents.ReqTakeFromMarket;
import it.polimi.ingsw.common.reducedmodel.ReducedLeaderCard;

public class TakeFromMarketState extends CliState {

    @Override
    public void render(Cli cli) {
        ViewModel vm = cli.getViewModel();


        // print market and shelves
        cli.getPrinter().update(vm.getMarket());
        cli.getPrinter().showWarehouseShelves(vm.getLocalPlayerNickname());
        
        cli.getOut().println("Getting resources from the market:");

        boolean isValid = false;
        boolean isRow = false;
        int index = -1;
        String input;

        // get user input, parse row/col and index
        while (!isValid) {
            isValid = true;
            input = cli.prompt("'row' or 'column'? Your choice");
            
            if(input.equalsIgnoreCase("row"))
                isRow = true;
            else if(input.equalsIgnoreCase("column"))
                isRow = false;
            else
                isValid = false;
        }
        isValid = false;

        while (!isValid) {
            input = cli.prompt("Index (1-based)");
            
            try {
                index = Integer.parseInt(input) - 1;

                if (index >= 0)
                    isValid = true;
            } catch (Exception e) {
            }
        }

        // get a list with the selected resources
        List<String> chosenResources = new ArrayList<>();
        if (isRow)
            chosenResources = vm.getMarket().getGrid().get(index);
        else {
            for (List<String> row : vm.getMarket().getGrid())
                chosenResources.add(row.get(index));
        }

        // if there's > 0 replaceable, get the active zeroleaders and prompt for replacements
        int blanksCount = (int) chosenResources.stream().filter(r -> r == vm.getMarket().getReplaceableResType()).count();
        Map<String, Integer> replacements = new HashMap<>();

        // for (String res : chosenResources)
        //     replacements.compute(res, (k, v) -> v == null ? 1 : v++);

        if (blanksCount > 0) {
            List<ReducedLeaderCard> zeroLeaders = vm.getPlayerLeaderCards(vm.getLocalPlayerNickname()).stream()
                .filter(c -> c.isActive() &&
                               c.getLeaderType() == ZeroLeader.class.getSimpleName()).toList();
            
            if (zeroLeaders.size() > 0) {
                cli.getPrinter().printOwnedLeaders(zeroLeaders);
                cli.getOut().println("These are the active leaders you can use to replace blank resources.");
                

                input = "";
                while (input != "y" || input != "n")
                    input = cli.prompt("Replace resources? [y/n]");
                if (input == "n") {
                    replacements = cli.promptResources(
                        blanksCount,
                        zeroLeaders.stream().map(c -> c.getResourceType()).toList());
                }
            }
        }

        // replacements = replacements.entrySet().stream().filter(e -> e.getKey().equals(cli.getViewModel().getMarket().getReplaceableResType())).collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));

        // remove the replaceable resources from the selected ones
        chosenResources = chosenResources.stream().filter(r -> !r.equals(vm.getMarket().getReplaceableResType())).toList();

        Map<String, Integer> totalRes = new HashMap<>(replacements);
        chosenResources.forEach(r -> totalRes.compute(r, (res, c) -> c == null ? 1 : c + 1));

        cli.dispatch(new ReqTakeFromMarket(isRow, index, replacements, cli.promptShelves(totalRes)));
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

    @Override
    public void on(Cli cli, UpdateResourceContainer event) {
        super.on(cli, event);

        cli.getPrinter().update(event.getResContainer());
    }

    @Override
    public void on(Cli cli, UpdateMarket event) {
        super.on(cli, event);

        cli.getPrinter().update(event.getMarket());
    }

    // UpdateFaithPoints
}
