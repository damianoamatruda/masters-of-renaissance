package it.polimi.ingsw.client.cli;

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

            input = Cli.prompt(out, in, "Which resource?");
            try {
                amount = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please input an integer.");
                continue;
            }

            //build shelves
            chosen += amount;
        }

        //build event
    }
}
