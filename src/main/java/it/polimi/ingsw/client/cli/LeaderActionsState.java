package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.cli.components.LeadersHand;
import it.polimi.ingsw.client.cli.components.Menu;
import it.polimi.ingsw.common.events.mvevents.UpdateAction;
import it.polimi.ingsw.common.events.mvevents.errors.ErrActiveLeaderDiscarded;
import it.polimi.ingsw.common.events.vcevents.ReqLeaderAction;

import java.util.LinkedHashMap;
import java.util.Map;

import static it.polimi.ingsw.client.cli.Cli.center;

public class LeaderActionsState extends CliController {
    private final CliController sourceState;
    private int leaderId;

    public LeaderActionsState(CliController sourceState) {
        this.sourceState = sourceState;
    }

    public void render() {
        cli.getOut().println();
        cli.getOut().println(center("~ Leader Actions ~"));

        cli.getOut().println();
        Map<Character, Menu.Entry> entries = new LinkedHashMap<>();
        entries.put('A', new Menu.Entry("Activate leader", cli1 -> executeLeaderAction(true)));
        entries.put('D', new Menu.Entry("Discard leader", cli1 -> executeLeaderAction(false)));
        new Menu(entries, cli1 -> cli1.setController(sourceState, false)).render();
    }

    private void executeLeaderAction(boolean isActivate) {
        new LeadersHand(vm.getPlayerLeaderCards(vm.getLocalPlayerNickname())).render();

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
        cli.setController(sourceState, true);
    }
}
