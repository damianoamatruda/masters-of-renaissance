package it.polimi.ingsw.client.cli;

import java.io.PrintStream;
import java.util.*;

public class SetupLeadersState extends CliState {
    @Override
    public void render(Cli cli, PrintStream out, Scanner in) {
        //print that says player has X resources, and Y leaders of choice. But who says how much are X and Y?

        List<Integer> leaders = new ArrayList<>();

        int chosen = 0;
        while(chosen < 2) {
            String input = Cli.prompt(out, in, "Which leaders?");
            try {
                int id = Integer.parseInt(input);
                leaders.add(id);
                chosen++;
            } catch (NumberFormatException e) {
                System.out.println("Please input an integer.");
            }
        }

        //build event and send
        //if error from server, repeat
    }
}
