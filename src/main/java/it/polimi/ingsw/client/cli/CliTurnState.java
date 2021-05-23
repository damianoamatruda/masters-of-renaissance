package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.common.events.mvevents.errors.ErrActiveLeaderDiscarded;
import it.polimi.ingsw.common.events.vcevents.ReqLeaderAction;

import java.io.PrintStream;
import java.util.Scanner;

public abstract class CliTurnState extends CliState {
    private int leaderId;

    public void leaderAction(Cli cli, PrintStream out, Scanner in) {
        String input;
        do {
            input = cli.prompt(out, in, "Leader action. A = Activate, D = Discard");
        } while (!input.toUpperCase().startsWith("A") && !input.toUpperCase().startsWith("D"));

        boolean isActivate = input.toUpperCase().startsWith("A");

        while (true) {
            input = cli.prompt(out, in, "Which leader?");
            try {
                leaderId = Integer.parseInt(input);
                cli.dispatch(new ReqLeaderAction(leaderId, isActivate));
                break;
            } catch (NumberFormatException e) {
                System.out.println("Please input an integer.");
            }
        }
    }

    @Override
    public void on(Cli cli, ErrActiveLeaderDiscarded event) {
        cli.repeatState(String.format("Active leader %d tried to be discarded.", leaderId));
    }
}
