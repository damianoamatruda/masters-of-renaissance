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
            cli.getOut().println(center("Waiting for all players to finish their setup..."));
            return;
        }

        cli.getOut().println();
        cli.getOut().println(center("~ Your turn ~"));

        cli.getOut().println();
        Map<Character, Menu.Entry> entries = new LinkedHashMap<>();
        entries.put('1', new Menu.Entry("Take Market Resources", () -> cli.setController(new TakeFromMarketController(this), false)));
        entries.put('2', new Menu.Entry("Buy a Development Card", () -> cli.setController(new BuyDevelopmentCardController(this), false)));
        entries.put('3', new Menu.Entry("Activate Productions", () -> cli.setController(new ActivateProductionsController(this), false)));
        if(vm.getLocalPlayer().map(vm::getPlayerLeaderCards).orElseThrow().stream().anyMatch(l -> !l.isActive()))
            entries.put('L', new Menu.Entry("Leader Actions", () -> cli.setController(new LeaderActionsController(this), false)));
        entries.put('S', new Menu.Entry("Swap Shelves", () -> cli.setController(new SwapShelvesController(this), false)));
        entries.put('Q', new Menu.Entry("Quit to Title", this::quitToTitle));
        new Menu(entries, this::quitToTitle).render();
    }
}
