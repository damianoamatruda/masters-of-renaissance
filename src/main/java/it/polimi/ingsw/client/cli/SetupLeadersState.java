package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.common.events.vcevents.ReqChooseLeaders;

import java.util.ArrayList;
import java.util.List;

public class SetupLeadersState extends CliState {
    private final int leadersToChoose;

    public SetupLeadersState(int leadersToChoose) {
        this.leadersToChoose = leadersToChoose;
    }

    @Override
    public void render(Cli cli) {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

        cli.getOut().println("\nChoosing leaders hand.");
        cli.getOut().println("Please input leader card IDs from the ones assigned to you.\n");

        if (cli.getViewModel().getPlayerLeaders(cli.getViewModel().getNickname()) != null)
//            cli.getViewModel().getPlayerLeaders(cli.getViewModel().getNickname()).forEach(printer::update);
            cli.getPrinter().printOwnedLeaders(cli.getViewModel().getPlayerLeaders(cli.getViewModel().getNickname()));

        List<Integer> leaders = new ArrayList<>();

        int chosen = 0;
        while (chosen < cli.getViewModel().getSetup(cli.getViewModel().getNickname()).getChosenLeadersCount()) {
            String input = cli.prompt((leadersToChoose - chosen) + " leader cards left to be chosen, which would you like to add? ID");
            try {
                int id = Integer.parseInt(input);
                leaders.add(id);
                chosen++;
            } catch (NumberFormatException e) {
                cli.getOut().println("Please input a numerical ID.");
            }
        }

        cli.dispatch(new ReqChooseLeaders(leaders));
        //build event and send
        //if error from server, repeat
    }
}
