package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.cli.components.Menu;
import it.polimi.ingsw.common.events.mvevents.*;

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

    @Override
    public void on(Cli cli, UpdateSetupDone event) {
        /* if the client got here it means it has finished the local setup and
           it switched to this state (it happens when the local player is the current player).
           When it got here, other players' setup was still ongoing,
           therefore this message is handled here */
        super.on(cli, event);
        cli.setState(new TurnBeforeActionState());
    }
}
