package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.cli.components.Menu;
import it.polimi.ingsw.common.events.mvevents.*;
import it.polimi.ingsw.common.events.vcevents.ReqSwapShelves;

import java.util.LinkedHashMap;
import java.util.Map;

public class TurnBeforeActionState extends CliTurnState {
    @Override
    public void render(Cli cli) {
        if (!cli.getViewModel().isSetupDone()) {
            cli.getOut().println("Waiting for all players to finish their setup...");
            return;
        }

        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        cli.getOut().println(Cli.center("\n\nAvailable actions:\n"));

        Map<Character, Menu.Entry> entries = new LinkedHashMap<>();
        entries.put('1', new Menu.Entry("Take market resources", cli1 -> cli.setState(new TakeFromMarketState())));
        entries.put('2', new Menu.Entry("Buy a card", cli1 -> cli.setState(new BuyDevelopmentCardState())));
        entries.put('3', new Menu.Entry("Activate production", cli1 -> cli.setState(new ActivateProductionsState())));
        entries.put('L', new Menu.Entry("Leader action", this::leaderAction));
        entries.put('S', new Menu.Entry("Swap shelves", this::swapShelves));

        new Menu(entries).render(cli);
    }

    private void swapShelves(Cli cli) {
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

            cli.repeatState("Swap request sent...");
        }
    }

    @Override
    public void on(Cli cli, UpdateSetupDone event) {
        cli.getViewModel().setSetupDone(true);
        cli.setState(new TurnBeforeActionState());
    }

    @Override
    public void on(Cli cli, UpdateCurrentPlayer event) {
        
    }
}
