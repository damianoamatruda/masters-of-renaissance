package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.cli.components.Menu;

import java.util.LinkedHashMap;
import java.util.Map;

import static it.polimi.ingsw.client.cli.Cli.center;

public class TurnBeforeActionState extends CliTurnState {
    @Override
    public void render() {
        if (!vm.isSetupDone().get()) {
            cli.getOut().println();
            cli.getOut().println("Waiting for all players to finish their setup...");
            return;
        }

        cli.getOut().println();
        cli.getOut().println(center("~ Your turn ~"));

        cli.getOut().println();
        Map<Character, Menu.Entry> entries = new LinkedHashMap<>();
        entries.put('1', new Menu.Entry("Take Market Resources", cli1 -> cli1.setController(new TakeFromMarketState(this), false)));
        entries.put('2', new Menu.Entry("Buy a Development Card", cli1 -> cli1.setController(new BuyDevelopmentCardState(this), false)));
        entries.put('3', new Menu.Entry("Activate Productions", cli1 -> cli1.setController(new ActivateProductionsState(this), false)));
        entries.put('L', new Menu.Entry("Leader Actions", cli1 -> cli1.setController(new LeaderActionsState(this), false)));
        entries.put('S', new Menu.Entry("Swap Shelves", cli1 -> cli1.setController(new SwapShelvesState(this), false)));
        entries.put('Q', new Menu.Entry("Quit to Title", this::quitToTitle));
        new Menu(entries, this::quitToTitle).render();
    }
}
