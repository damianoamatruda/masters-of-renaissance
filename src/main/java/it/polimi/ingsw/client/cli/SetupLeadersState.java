package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.common.events.vcevents.ReqChooseLeaders;

import java.io.PrintStream;
import java.util.*;

public class SetupLeadersState extends CliState {
    private int leadersToChoose;

    public SetupLeadersState(int leadersToChoose) {
        this.leadersToChoose = leadersToChoose;
    }

    @Override
    public void render(Cli cli, PrintStream out, Scanner in) {
        //print that says player has X resources, and Y leaders of choice. But who says how much are X and Y?

        List<Integer> leaders = new ArrayList<>();

        int chosen = 0;
        while(chosen < leadersToChoose) {
            String input = Cli.prompt(out, in, "Choose a leader. " + (leadersToChoose - chosen) + " left");
            try {
                int id = Integer.parseInt(input);
                leaders.add(id);
                chosen++;
            } catch (NumberFormatException e) {
                System.out.println("Please input an integer.");
            }
        }

        cli.sendToView(new ReqChooseLeaders(leaders));
        //build event and send
        //if error from server, repeat
    }
}
