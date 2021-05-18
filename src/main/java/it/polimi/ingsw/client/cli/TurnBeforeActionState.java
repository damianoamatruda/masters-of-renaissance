package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.common.events.vcevents.ReqActivateProduction;
import it.polimi.ingsw.common.events.vcevents.ReqBuyDevCard;
import it.polimi.ingsw.common.events.vcevents.ReqTakeFromMarket;

import java.io.PrintStream;
import java.util.*;

public class TurnBeforeActionState extends CliState {
    @Override
    public void render(Cli cli, PrintStream out, Scanner in) {
        Map<Character, Menu.Entry> entries = new LinkedHashMap<>();
        entries.put('1', new Menu.Entry("Buy a card", (menu) -> buyCard(cli, out, in)));
        entries.put('2', new Menu.Entry("Obtain resources", (menu) -> getResources(cli, out, in)));
        entries.put('3', new Menu.Entry("Activate production", (menu) -> produce(cli, out, in)));
        entries.put('L', new Menu.Entry("Leader action", (menu) -> leaderAction(cli, out, in)));

        new Menu(entries).render(cli, out, in); // Will be eventually changed to sth different than a menu, no worries

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

        shelves = promptShelves(cli, out, in);

        //build request event
        cli.sendToView(new ReqBuyDevCard(color, level, slot, shelves));
    }

    private void getResources(Cli cli, PrintStream out, Scanner in) {
        boolean isValid = false;
        boolean isRow = false;
        int index = -1;

        while (!isValid) {
            isValid = true;
            String input = Cli.prompt(out, in, "Choose a row or a column (example: row 4)");
            String[] splitInput = input.split(" ", 2);
            if(splitInput[0].equalsIgnoreCase("row")) {
                isRow = true;
            }
            else if(splitInput[0].contains("col".toLowerCase())) {
                isRow = false;
            }
            else isValid = false;

            try {
                index = Integer.parseInt(splitInput[1]);
            } catch (NumberFormatException e) {
                isValid = false;
            }
        }
        cli.sendToView(new ReqTakeFromMarket(isRow, index, null /* Remove null obv */, promptShelves(cli, out, in)));
    }

    private void produce(Cli cli, PrintStream out, Scanner in) {
//        cli.sendToView(new ReqActivateProduction());
    }

    private void leaderAction(Cli cli, PrintStream out, Scanner in) {
//        cli.sendToView();   //either activate or discard
    }

    private Map<Integer, Map<String, Integer>> promptShelves(Cli cli, PrintStream out, Scanner in) {
        final Map<Integer, Map<String, Integer>> shelves = new HashMap<>();
        int container;
        String resource;
        int amount;

        String input = Cli.prompt(out, in, "Which container?");
        while (!input.equalsIgnoreCase("send")) {
            try {
                container = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please input an integer.");
                continue;
            }

            resource = Cli.prompt(out, in, "Which resource?");

            input = Cli.prompt(out, in, "How many?");
            try {
                amount = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please input an integer.");
                continue;
            }

            if(shelves.containsKey(container)) {
                if(shelves.get(container).containsKey(resource)) {
                    shelves.get(container).replace(resource, shelves.get(container).get(resource) + amount);
                } else {
                    shelves.get(container).put(resource, amount);
                }
            } else {
                shelves.put(container, Map.of(resource, amount));
            }
        }
        return shelves;
    }
}
