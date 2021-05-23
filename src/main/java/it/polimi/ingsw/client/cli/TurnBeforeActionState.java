package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.cli.components.Menu;
import it.polimi.ingsw.common.events.mvevents.*;
import it.polimi.ingsw.common.events.vcevents.ReqActivateProduction;
import it.polimi.ingsw.common.events.vcevents.ReqBuyDevCard;
import it.polimi.ingsw.common.events.vcevents.ReqSwapShelves;
import it.polimi.ingsw.common.events.vcevents.ReqTakeFromMarket;
import it.polimi.ingsw.common.reducedmodel.ReducedProductionRequest;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TurnBeforeActionState extends CliTurnState {
    @Override
    public void render(Cli cli) {
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        cli.getOut().println(Cli.center("\n\nAvailable actions:\n"));

        Map<Character, Menu.Entry> entries = new LinkedHashMap<>();
        entries.put('1', new Menu.Entry("Buy a card", this::buyCard));
        entries.put('2', new Menu.Entry("Take market resources", this::getResources));
        entries.put('3', new Menu.Entry("Activate production", this::produce));
        entries.put('L', new Menu.Entry("Leader action", this::leaderAction));
        entries.put('S', new Menu.Entry("Swap shelves", this::swapShelves));

        new Menu(entries).render(cli);
    }

    private void buyCard(Cli cli) {
        cli.getOut().println("Buying a development card.");

        cli.getPrinter().update(cli.getCache().getDevCardGrid());

        cli.getOut().println("\nChoose parameters:");
        //prompt for parameters
        final Map<Integer, Map<String, Integer>> shelves;
        int level = 0, slot = 0;

        String color = cli.prompt("Card color");
        boolean isNumber = false;
        while (!isNumber) {
            try {
                String input = cli.prompt("Card level");
                level = Integer.parseInt(input);
                isNumber = true;
            } catch (NumberFormatException e) {
                cli.getOut().println("Please input an integer.");
            }
        }

        isNumber = false;
        while (!isNumber) {
            try {
                String input = cli.prompt("Player board slot to assign to the card");
                slot = Integer.parseInt(input);
                isNumber = true;
            } catch (NumberFormatException e) {
                cli.getOut().println("Please input an integer.");
            }
        }

        cli.getOut().println("Resources need to be paid.");
        cli.getOut().println("Please specify how many resources to take from which container.");

        cli.getPrinter().showWarehouseShelves(cli.getCache().getNickname());
        cli.getPrinter().showStrongbox(cli.getCache().getNickname());

        shelves = cli.promptShelves();

        //build request event
        cli.dispatch(new ReqBuyDevCard(color, level, slot, shelves));
    }

    private void getResources(Cli cli) {
        cli.getOut().println("Getting resources from the market:");

        cli.getPrinter().update(cli.getCache().getMarket());
        cli.getPrinter().showWarehouseShelves(cli.getCache().getNickname());

        boolean isValid = false;
        boolean isRow = false;
        int index = -1;
        String input;

        while (!isValid) {
            isValid = true;
            input = cli.prompt("Choose a row or a column (example: row 1)");
            String[] splitInput = input.split(" ", 2);
            if(splitInput[0].equalsIgnoreCase("row")) {
                isRow = true;
            }
            else if(splitInput[0].equalsIgnoreCase("col")) {
                isRow = false;
            }
            else isValid = false;

            try {
                index = Integer.parseInt(splitInput[1]) - 1;
            } catch (Exception e) {
                isValid = false;
            }
        }

        // if has ZeroLeaders active: (if branch to be implemented)
        Map<String, Integer> replacements = cli.promptResources();

        cli.dispatch(new ReqTakeFromMarket(isRow, index, replacements, cli.promptShelves()));
    }

    private void produce(Cli cli) {
        List<ReducedProductionRequest> requests = new ArrayList<>();
        int productionid;
        String input = "";
        while (!input.equalsIgnoreCase("Y")) {
            input = cli.prompt("Choose a production number");
            try {
                productionid = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                cli.getOut().println("Please input an integer.");
                continue;
            }
            cli.getOut().println("-- Input replacement --");
            Map<String, Integer> inputReplacement = cli.promptResources();
            cli.getOut().println("-- Output replacement --");
            Map<String, Integer> outputReplacement = cli.promptResources();

            cli.getOut().println("-- Containers (from which to pay) --");
            Map<Integer, Map<String, Integer>> shelves = cli.promptShelves();

            requests.add(new ReducedProductionRequest(productionid, inputReplacement, outputReplacement, shelves));
            input = cli.prompt("Are you done? [Y/*]");
        }

        cli.dispatch(new ReqActivateProduction(requests));
    }

    private void swapShelves(Cli cli) {
        cli.getOut().println("Swapping shelves:");

        cli.getPrinter().showWarehouseShelves(cli.getCache().getNickname());

        int shelfid1, shelfid2;
        boolean isValid = false;


        while (!isValid) {
            String input = cli.prompt("Choose shelf 1");
            try {
                shelfid1 = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                cli.getOut().println("Please input an integer.");
                continue;
            }

            input = cli.prompt("Choose shelf 2");
            try {
                shelfid2 = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                cli.getOut().println("Please input an integer.");
                continue;
            }

            isValid = true;
            cli.dispatch(new ReqSwapShelves(shelfid1, shelfid2));

            cli.repeatState("Swap request sent...");
        }
    }

    @Override
    public void on(Cli cli, UpdateDevCardSlot event) {
        super.on(cli, event);
        cli.setState(new TurnAfterActionState());
    }

    @Override
    public void on(Cli cli, UpdateMarket event) {
        super.on(cli, event);
        cli.setState(new TurnAfterActionState());
    }

    @Override
    public void on(Cli cli, UpdateResourceContainer event) {
        super.on(cli, event);
        cli.setState(new TurnAfterActionState()); /* Update comes from my production activation (base action) */
    }

    @Override
    public void on(Cli cli, UpdateSetupDone event) {
    }

    @Override
    public void on(Cli cli, UpdateCurrentPlayer event) {
    }
}
