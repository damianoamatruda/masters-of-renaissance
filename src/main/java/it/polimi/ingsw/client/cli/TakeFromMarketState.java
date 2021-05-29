package it.polimi.ingsw.client.cli;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.polimi.ingsw.client.ViewModel.ViewModel;
import it.polimi.ingsw.common.backend.model.leadercards.ZeroLeader;
import it.polimi.ingsw.common.events.mvevents.UpdateAction;
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

                if (index > 0)
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
        Map<String, Integer> replacements = null;
        if (blanksCount > 0) {
            List<ReducedLeaderCard> zeroLeaders = vm.getPlayerLeaderCards(vm.getLocalPlayerNickname()).stream()
                .filter(c -> c.isActive() &&
                               c.getLeaderType() == ZeroLeader.class.getSimpleName()).toList();
            
            if (zeroLeaders.size() > 0) {
                cli.getPrinter().printOwnedLeaders(zeroLeaders);
                cli.getOut().println("These are the active leaders you can use to replace blank resources.");
                
                replacements = promptResources(cli,
                    blanksCount,
                    zeroLeaders.stream().map(c -> c.getResourceType()).toList());
            }
        }

        // remove the replaceable resources from the selected ones
        chosenResources = chosenResources.stream().filter(r -> r != vm.getMarket().getReplaceableResType()).toList();

        Map<String, Integer> totalRes = new HashMap<>(replacements);
        chosenResources.forEach(r -> totalRes.compute(r, (res, c) -> c == null ? 1 : c + 1));

        cli.getPrinter().showWarehouseShelves(vm.getLocalPlayerNickname());
        cli.getOut().println("These are your shelves.");

        cli.dispatch(new ReqTakeFromMarket(isRow, index, replacements, promptShelves(cli, totalRes)));
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


    private Map<Integer, Map<String, Integer>> promptShelves(Cli cli, Map<String, Integer> totalRes) {
        Map<Integer, Map<String, Integer>> shelves = new HashMap<>();

        totalRes.keySet().forEach(r -> cli.getOut().println(r));
        cli.getOut().println("These are the resource types to be stored.");

        // prompt user for resource -> count -> shelf to put it in
        int totalResCount = totalRes.entrySet().stream().mapToInt(e -> e.getValue().intValue()).sum(),
            allocResCount = 0;
        while (allocResCount < totalResCount) { // does not check for overshooting
            int count = 0, shelfID = -1; String res = "";
            while (!totalRes.keySet().contains(res))
                res = cli.prompt("Resource to store");
            
            while (count < 1) {
                String input = cli.prompt("How many? Count (> 0):");
            
                try {
                    count = Integer.parseInt(input);
                } catch (Exception e) { }
            }

            while (shelfID < 0) {
                String input = cli.prompt("How many? Count (> 0):");
            
                try {
                    shelfID = Integer.parseInt(input);
                } catch (Exception e) { }
            }

            String r = res; int c = count; // rMap.put below complained they weren't final
            shelves.compute(shelfID, (sid, rMap) -> {
                if (rMap == null)
                    rMap = new HashMap<>();

                rMap.compute(r, (k, v) -> v == null ? c : c + v);
                return rMap;
            });
        }

        return shelves;
    }

    Map<String, Integer> promptResources(Cli cli, int replaceable, List<String> replacements) {
        Map<String, Integer> replacedRes = new HashMap<>();

        String input = "";
        while (input != "y" || input != "n")
            input = cli.prompt("Replace resources? [y/n]");
        if (input == "n")
            return null;

        List<String> resourceNames = cli.getViewModel().getResourceTypes().stream().map(r -> r.getName()).toList();
        
        resourceNames.forEach(n -> cli.getOut().println(n));
        cli.getOut().println("These are the resources you can replace blanks with.");
        while (replaceable > 0) {
            int count = 0; String res = "";

            while (!resourceNames.contains(res))
                res = cli.prompt("Choose a replacement resource");

            while (count < 1) {
                input = cli.prompt("How many blanks should this replace? Count (> 0):");
            
                try {
                    count = Integer.parseInt(input);
                } catch (Exception e) { }
            }
            
            replacedRes.put(res, count);
            replaceable -= count; // does not check the "too many" scenario
        }

        return replacedRes;
    }
}
