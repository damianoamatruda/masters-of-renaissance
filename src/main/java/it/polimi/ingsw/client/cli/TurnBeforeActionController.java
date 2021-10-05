package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.cli.components.Menu;

import java.util.LinkedHashMap;
import java.util.Map;

import static it.polimi.ingsw.client.cli.Cli.center;

public class TurnBeforeActionController extends TurnController {
    @Override
    public void render() {
        if (!vm.isSetupDone()) {
            cli.getOut().println();
            cli.getOut().println(center("~ Setup done ~"));

            cli.getOut().println();
            cli.getOut().println(center("Waiting for other players to finish their setup..."));
            return;
        }

        cli.getOut().println();
        cli.getOut().println(center("~ Your turn ~"));

        cli.getOut().println();
        Map<Character, Menu.Entry> entries = new LinkedHashMap<>();
        entries.put('1', new Menu.Entry("Take Market Resources", () -> cli.setController(new TakeFromMarketController(this))));
        entries.put('2', new Menu.Entry("Buy a Development Card", () -> cli.setController(new BuyDevelopmentCardController(this))));
        entries.put('3', new Menu.Entry("Activate Productions", () -> cli.setController(new ActivateProductionsController(this))));
        entries.putAll(getNonMandatoryMenuEntries());
        entries.put('Q', new Menu.Entry("Quit to Title", this::quitToTitle));
        new Menu(entries, this::quitToTitle).render();
    }
}
