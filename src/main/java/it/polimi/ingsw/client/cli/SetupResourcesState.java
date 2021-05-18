package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.common.events.vcevents.ReqChooseLeaders;
import it.polimi.ingsw.common.events.vcevents.ReqChooseResources;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class SetupResourcesState extends CliState {
    private final int choosable;

    public SetupResourcesState(int choosable) {
        this.choosable = choosable;
    }
    @Override
    public void render(Cli cli, PrintStream out, Scanner in) {
        final Map<Integer, Map<String, Integer>> shelves = new HashMap<>();

        int chosen = 0;
        while(chosen < choosable) {
            int container;
            int amount;
            String input = Cli.prompt(out, in, "Which container?");
            try {
                container = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please input an integer.");
                continue;
            }

            String resource = Cli.prompt(out, in, "Which resource?");

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
            chosen += amount;
        }

        cli.sendToView(new ReqChooseResources(shelves));
    }
}
