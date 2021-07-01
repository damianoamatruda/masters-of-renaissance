package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.cli.components.LeadersHand;
import it.polimi.ingsw.client.cli.components.Menu;
import it.polimi.ingsw.common.events.mvevents.UpdateAction;
import it.polimi.ingsw.common.events.mvevents.errors.ErrActiveLeaderDiscarded;
import it.polimi.ingsw.common.events.vcevents.ReqLeaderAction;

import java.util.LinkedHashMap;
import java.util.Map;

import static it.polimi.ingsw.client.cli.Cli.center;

public class LeaderActionsController extends CliController {
    private final CliController sourceController;
    private int leaderId;

    public LeaderActionsController(CliController sourceController) {
        this.sourceController = sourceController;
    }

    public void render() {
        cli.getOut().println();
        cli.getOut().println(center("~ Leader Actions ~"));

        cli.getOut().println();
        Map<Character, Menu.Entry> entries = new LinkedHashMap<>();
        entries.put('A', new Menu.Entry("Activate leader", () -> executeLeaderAction(true)));
        entries.put('D', new Menu.Entry("Discard leader", () -> executeLeaderAction(false)));
        new Menu(entries, () -> cli.setController(sourceController, false)).render();
    }

    private void executeLeaderAction(boolean isActivate) {
        new LeadersHand(vm.getLocalPlayer().map(vm::getPlayerLeaderCards).orElseThrow()).render();

        cli.getOut().println();
        cli.promptInt("Leader").ifPresentOrElse(leaderId -> {
            this.leaderId = leaderId;
            cli.getUi().dispatch(new ReqLeaderAction(leaderId, isActivate));
        }, () -> cli.setController(this, false));
    }

    @Override
    public void on(ErrActiveLeaderDiscarded event) {
        cli.reloadController(String.format("Leader card with ID %d cannot be discarded: leader card is active.", leaderId));
    }

    @Override
    public void on(UpdateAction event) {
        cli.setController(sourceController, true);
    }
}
