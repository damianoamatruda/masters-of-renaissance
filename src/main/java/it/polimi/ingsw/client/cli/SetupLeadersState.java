package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.ReducedObjectPrinter;
import it.polimi.ingsw.common.events.vcevents.ReqChooseLeaders;
import it.polimi.ingsw.common.reducedmodel.ReducedGame;

import java.io.PrintStream;
import java.util.*;

public class SetupLeadersState extends CliState {
    private int leadersToChoose;

    public SetupLeadersState(int leadersToChoose) {
        this.leadersToChoose = leadersToChoose;
    }

    @Override
    public void render(Cli cli, PrintStream out, Scanner in, ReducedGame cache, ReducedObjectPrinter printer) {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

        System.out.println("Choosing leaders hand.");
        System.out.println("Please input leader card IDs from the ones assigned to you.");

        List<Integer> leaders = new ArrayList<>();

        int chosen = 0;
        while(chosen < cache.getLeadersToChoose()) {
            String input = cli.prompt(out, in,
                (leadersToChoose - chosen) + " leader cards left to be chosen, which would you like to add? ID");
            try {
                int id = Integer.parseInt(input);
                leaders.add(id);
                chosen++;
            } catch (NumberFormatException e) {
                System.out.println("Please input a numerical ID.");
            }
        }

        cli.sendToView(new ReqChooseLeaders(leaders));
        //build event and send
        //if error from server, repeat
    }
}
