package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.cli.components.LeadersHand;
import it.polimi.ingsw.client.cli.components.Menu;
import it.polimi.ingsw.common.events.mvevents.errors.ErrActiveLeaderDiscarded;
import it.polimi.ingsw.common.events.vcevents.ReqLeaderAction;
import it.polimi.ingsw.common.events.vcevents.ReqSwapShelves;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class CliTurnState extends CliState {
    private int leaderId;

    public void leaderActions(Cli cli) {
        cli.getOut().println();
        cli.getOut().println(Cli.center("Leader actions:\n"));

        Map<Character, Menu.Entry> entries = new LinkedHashMap<>();
        entries.put('A', new Menu.Entry("Activate leader", cli1 -> executeAction(cli, true)));
        entries.put('D', new Menu.Entry("Discard leader", cli1 -> executeAction(cli, false)));

        new Menu(entries).render(cli);
    }

    private void executeAction(Cli cli, boolean isActivate) {
        new LeadersHand(cli.getViewModel().getPlayerLeaderCards(cli.getViewModel().getLocalPlayerNickname())).render(cli);

        cli.getOut().println();
        leaderId = cli.promptInt("Leader");

        cli.dispatch(new ReqLeaderAction(leaderId, isActivate));
    }

    void swapShelves(Cli cli) {
        cli.getOut().println("Swapping shelves.");
        cli.getOut().println();
        cli.showShelves(cli.getViewModel().getLocalPlayerNickname());
        cli.getOut().println();
        int shelfId1 = cli.promptInt("First shelf");
        int shelfId2 = cli.promptInt("Second shelf");
        cli.dispatch(new ReqSwapShelves(shelfId1, shelfId2));
    }

    @Override
    public void on(Cli cli, ErrActiveLeaderDiscarded event) {
        cli.repeatState(String.format("Active leader %d tried to be discarded.", leaderId));
    }
}
