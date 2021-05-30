package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.viewmodel.ViewModel;
import it.polimi.ingsw.common.events.mvevents.UpdateAction;
import it.polimi.ingsw.common.events.mvevents.UpdateAction.ActionType;
import it.polimi.ingsw.common.events.mvevents.errors.ErrAction;
import it.polimi.ingsw.common.events.mvevents.errors.ErrAction.ErrActionReason;
import it.polimi.ingsw.common.events.vcevents.ReqChooseResources;

import java.util.HashMap;
import java.util.Map;

public class SetupResourcesState extends CliState {
    private final int choosable;

    public SetupResourcesState(int choosable) {
        this.choosable = choosable;
    }

    @Override
    public void render(Cli cli) {
        cli.getOut().println("\nChoosing starting resources.");
        cli.getOut().println("You can choose " + choosable + " resources. What's your choice?");
        
        Map<Integer, Map<String, Integer>> shelves = promptShelves(cli);

        cli.dispatch(new ReqChooseResources(shelves));
    }

    @Override
    public void on(Cli cli, ErrAction event) {
        if (event.getReason() != ErrActionReason.LATE_SETUP_ACTION)
            throw new RuntimeException("Resources setup: ErrAction received with reason not LATE_SETUP_ACTION.");
            
        if (cli.getViewModel().getCurrentPlayer() == cli.getViewModel().getLocalPlayerNickname())
            cli.setState(new TurnBeforeActionState());
        else
            cli.setState(new WaitingAfterTurnState());
    }

    // ErrObjectNotOwned handled in clistate
    // ErrNoSuchEntity handled in clistate
    // ErrResourceReplacement handled in clistate
    // ErrReplacedTransRecipe handled in clistate
    // ErrInitialChoice handled in clistate
    // ErrResourceTransfer handled in clistate

    @Override
    public void on(Cli cli, UpdateAction event) {
        if (event.getAction() != ActionType.CHOOSE_RESOURCES)
            throw new RuntimeException("Resources setup: UpdateAction received with action type not CHOOSE_RESOURCES.");

        if (cli.getViewModel().getCurrentPlayer() == cli.getViewModel().getLocalPlayerNickname())
            cli.setState(new TurnBeforeActionState());
        else
            cli.setState(new WaitingAfterTurnState());
    }

    Map<Integer, Map<String, Integer>> promptShelves(Cli cli) {
        ViewModel vm = cli.getViewModel();

        cli.getPrinter().showWarehouseShelves(vm.getLocalPlayerNickname());
        vm.getResourceTypes().forEach(r -> cli.getOut().println(r.getName()));

        cli.getOut().println("Choose mapping shelf-resource-quantity:");
        final Map<Integer, Map<String, Integer>> shelves = new HashMap<>();
        int container;
        String resource;
        int amount;

        String input = "";
        while (!input.equalsIgnoreCase("Y")) {
            input = cli.prompt("Which container? (Input an ID, or else Enter to skip)");
            if (input.isEmpty())
                break;

            try {
                container = Integer.parseInt(input);
            //    int finalContainer = container;
            //    if(cache.getContainers().stream().filter(c -> c.getId() == finalContainer).findAny().isEmpty()) {
            //        cli.getOut().println("You do not own this container. Try again");
            //        continue;
            //    };
            } catch (NumberFormatException e) {
                cli.getOut().println("Please input an integer.");
                continue;
            }

            resource = cli.prompt("Resource type");

            input = cli.prompt("Resource amount");
            try {
                amount = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                cli.getOut().println("Please input an integer.");
                continue;
            }

            if(shelves.containsKey(container)) {
                if(shelves.get(container).containsKey(resource)) {
                    shelves.get(container).replace(resource, shelves.get(container).get(resource) + amount);
                } else {
                    shelves.get(container).put(resource, amount);
                }
            } else {
                Map<String, Integer> resourceMapping = new HashMap<>();
                resourceMapping.put(resource, amount);
                shelves.put(container, resourceMapping);
            }
            input = cli.prompt("Are you done choosing? [y/n]");
        }

        return shelves;
    }
}
