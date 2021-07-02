package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.cli.components.Menu;
import it.polimi.ingsw.common.events.mvevents.errors.ErrActiveLeaderDiscarded;
import it.polimi.ingsw.common.events.vcevents.ReqQuit;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class TurnController extends CliController {
    protected void quitToTitle() {
        cli.getUi().dispatch(new ReqQuit());
    }

    protected Map<Character, Menu.Entry> getNonMandatoryMenuEntries() {
        Map<Character, Menu.Entry> entries = new LinkedHashMap<>();

        if (vm.getLocalPlayer().map(vm::getPlayerLeaderCards).map(leaderCards -> leaderCards.stream().anyMatch(l -> !l.isActive())).orElse(false))
            entries.put('L', new Menu.Entry("Leader Actions", () -> cli.setController(new LeaderActionsController(this))));

        entries.put('S', new Menu.Entry("Swap Shelves", () -> cli.setController(new SwapShelvesController(this))));

        return entries;
    }

    @Override
    public void on(ErrActiveLeaderDiscarded event) {
        super.on(event);

        cli.reloadController("Active leader cannot be discarded.");
    }
}
