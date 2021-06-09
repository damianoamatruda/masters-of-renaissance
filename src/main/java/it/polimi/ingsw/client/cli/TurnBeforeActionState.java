package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.cli.components.Menu;

import java.util.LinkedHashMap;
import java.util.Map;

public class TurnBeforeActionState extends CliTurnState {
    @Override
    public void render(Cli cli) {
        if (!cli.getViewModel().isSetupDone()) {
            cli.getOut().println();
            cli.getOut().println("Waiting for all players to finish their setup...");
            return;
        }

        cli.getOut().println();
        cli.getOut().print(Cli.center("Available actions:"));

        cli.getOut().println();
        Map<Character, Menu.Entry> entries = new LinkedHashMap<>();
        entries.put('1', new Menu.Entry("Take Market Resources", cli1 -> cli1.setState(new TakeFromMarketState(this))));
        entries.put('2', new Menu.Entry("Buy a Card", cli1 -> cli1.setState(new BuyDevelopmentCardState(this))));
        entries.put('3', new Menu.Entry("Activate Production", cli1 -> cli1.setState(new ActivateProductionsState(this))));
        entries.put('L', new Menu.Entry("Leader Actions", cli1 -> cli1.setState(new LeaderActionsState(this))));
        entries.put('S', new Menu.Entry("Swap Shelves", cli1 -> cli1.setState(new SwapShelvesState(this))));
        entries.put('Q', new Menu.Entry("Quit to Title", this::quitToTitle));
        new Menu(entries, this::quitToTitle).render(cli);
    }
}
