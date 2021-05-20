package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.common.events.vcevents.ReqLeaderAction;

import java.io.PrintStream;
import java.util.Scanner;

public abstract class CliTurnState extends CliState {
    public void leaderAction(Cli cli, PrintStream out, Scanner in) {
        String input;
        do {
            input = cli.prompt(out, in, "Leader action. A = Activate, D = Discard");
        } while (!input.toUpperCase().startsWith("A") && !input.toUpperCase().startsWith("D"));

        boolean isActivate = input.toUpperCase().startsWith("A");

        while (true) {
            input = cli.prompt(out, in, "Which leader?");
            try {
                int leaderid = Integer.parseInt(input);
                cli.sendToView(new ReqLeaderAction(leaderid, isActivate));
                break;
            } catch (NumberFormatException e) {
                System.out.println("Please input an integer.");
            }
        }
    }
}
