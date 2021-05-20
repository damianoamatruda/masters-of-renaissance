package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.common.events.vcevents.ReqActivateProduction;
import it.polimi.ingsw.common.events.vcevents.ReqBuyDevCard;
import it.polimi.ingsw.common.events.vcevents.ReqSwapShelves;
import it.polimi.ingsw.common.events.vcevents.ReqTakeFromMarket;
import it.polimi.ingsw.common.reducedmodel.ReducedGame;
import it.polimi.ingsw.common.reducedmodel.ReducedProductionRequest;

import java.io.PrintStream;
import java.util.*;

public class TurnBeforeActionState extends CliTurnState {
    @Override
    public void render(Cli cli, PrintStream out, Scanner in, ReducedGame cache) {
        Map<Character, Menu.Entry> entries = new LinkedHashMap<>();
        entries.put('1', new Menu.Entry("Buy a card", (menu) -> buyCard(cli, out, in)));
        entries.put('2', new Menu.Entry("Take market resources", (menu) -> getResources(cli, out, in, cache)));
        entries.put('3', new Menu.Entry("Activate production", (menu) -> produce(cli, out, in)));
        entries.put('L', new Menu.Entry("Leader action", (menu) -> leaderAction(cli, out, in)));
        entries.put('S', new Menu.Entry("Swap shelves", (menu) -> swapShelves(cli, out, in)));

        new Menu(entries).render(cli, out, in, cache);

    }

    private void buyCard(Cli cli, PrintStream out, Scanner in) {
        //prompt for parameters
        final Map<Integer, Map<String, Integer>> shelves;
        int level = 0, slot = 0;

        String color = Cli.prompt(out, in, "What color?");
        boolean isNumber = false;
        while (!isNumber) {
            try {
                String input = Cli.prompt(out, in, "What level?");
                level = Integer.parseInt(input);
                isNumber = true;
            } catch (NumberFormatException e) {
                System.out.println("Please input an integer.");
            }
        }

        isNumber = false;
        while (!isNumber) {
            try {
                String input = Cli.prompt(out, in, "Which slot?");
                slot = Integer.parseInt(input);
                isNumber = true;
            } catch (NumberFormatException e) {
                System.out.println("Please input an integer.");
            }
        }

        shelves = cli.promptShelves(out, in);

        //build request event
        cli.sendToView(new ReqBuyDevCard(color, level, slot, shelves));
    }

    private void getResources(Cli cli, PrintStream out, Scanner in, ReducedGame cache) {
        boolean isValid = false;
        boolean isRow = false;
        int index = -1;
        String input;

        while (!isValid) {
            isValid = true;
            input = Cli.prompt(out, in, "Choose a row or a column (example: row 1)");
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
        Map<String,Integer> replacements = cli.promptResources(out, in);

        cli.sendToView(new ReqTakeFromMarket(isRow, index, replacements, cli.promptShelves(out, in)));
    }

    private void produce(Cli cli, PrintStream out, Scanner in) {
        List<ReducedProductionRequest> requests = new ArrayList<>();
        int productionid;
        String input = "";
        while (!input.equalsIgnoreCase("Y")) {
            input = Cli.prompt(out, in, "Choose a production number");
            try {
                productionid = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please input an integer.");
                continue;
            }
            System.out.println("-- Input replacement --");
            Map<String, Integer> inputReplacement = cli.promptResources(out, in);
            System.out.println("-- Output replacement --");
            Map<String, Integer> outputReplacement = cli.promptResources(out, in);

            System.out.println("-- Containers (from which to pay) --");
            Map<Integer, Map<String, Integer>> shelves = cli.promptShelves(out, in);

            requests.add(new ReducedProductionRequest(productionid, inputReplacement, outputReplacement, shelves));
            input = Cli.prompt(out, in, "Are you done? [Y/*]");
        }

        cli.sendToView(new ReqActivateProduction(requests));
    }

    private void swapShelves(Cli cli, PrintStream out, Scanner in) {
        int shelfid1, shelfid2;
        boolean isValid = false;


        while (!isValid) {
            String input = Cli.prompt(out, in, "Choose shelf 1");
            try {
                shelfid1 = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please input an integer.");
                continue;
            }

            input = Cli.prompt(out, in, "Choose shelf 2");
            try {
                shelfid2 = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please input an integer.");
                continue;
            }

            isValid = true;
            cli.sendToView(new ReqSwapShelves(shelfid1, shelfid2));

            cli.repeatState("Swap request sent...");
        }
    }
}
