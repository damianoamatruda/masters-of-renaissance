package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.common.events.mvevents.errors.ErrActiveLeaderDiscarded;
import it.polimi.ingsw.common.events.vcevents.ReqLeaderAction;
import it.polimi.ingsw.common.events.vcevents.ReqSwapShelves;

public abstract class CliTurnState extends CliState {
    private int leaderId;

    public void leaderAction(Cli cli) {
        String input;
        do {
            input = cli.prompt("Leader action. A = Activate, D = Discard");
        } while (!input.toUpperCase().startsWith("A") && !input.toUpperCase().startsWith("D"));

        boolean isActivate = input.toUpperCase().startsWith("A");

        while (true) {
            input = cli.prompt("Which leader?");
            try {
                leaderId = Integer.parseInt(input);
                cli.dispatch(new ReqLeaderAction(leaderId, isActivate));
                break;
            } catch (NumberFormatException e) {
                cli.getOut().println("Please input an integer.");
            }
        }
    }

    void swapShelves(Cli cli) {
        cli.getOut().println("Swapping shelves:");

        cli.getPrinter().showWarehouseShelves(cli.getViewModel().getLocalPlayerNickname());

        int shelfid1, shelfid2;
        boolean isValid = false;


        while (!isValid) {
            String input = cli.prompt("Choose shelf 1");
            try {
                shelfid1 = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                cli.getOut().println("Please input an integer.");
                continue;
            }

            input = cli.prompt("Choose shelf 2");
            try {
                shelfid2 = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                cli.getOut().println("Please input an integer.");
                continue;
            }

            isValid = true;
            cli.dispatch(new ReqSwapShelves(shelfid1, shelfid2));
        }
    }

    @Override
    public void on(Cli cli, ErrActiveLeaderDiscarded event) {
        cli.repeatState(String.format("Active leader %d tried to be discarded.", leaderId));
    }
}
