package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.cli.components.Menu;
import it.polimi.ingsw.common.events.mvevents.UpdateAction;
import it.polimi.ingsw.common.events.mvevents.UpdateCurrentPlayer;
import it.polimi.ingsw.common.events.vcevents.ReqEndTurn;

import java.util.LinkedHashMap;
import java.util.Map;

public class TurnAfterActionState extends CliTurnState {
    @Override
    public void render(Cli cli) {
        Map<Character, Menu.Entry> entries = new LinkedHashMap<>();
        entries.put('L', new Menu.Entry("Leader actions", this::leaderActions));
        entries.put('S', new Menu.Entry("Swap shelves", this::swapShelves));
        entries.put('E', new Menu.Entry("End turn", this::endTurn));

        new Menu(entries).render(cli);
    }

    private void endTurn(Cli cli) {
        cli.dispatch(new ReqEndTurn());
    }

    @Override
    public void on(Cli cli, UpdateAction event) {
        cli.setState(new TurnAfterActionState());
    }

    @Override
    public void on(Cli cli, UpdateCurrentPlayer event) {
        super.on(cli, event);
        if (cli.getViewModel().getLocalPlayerNickname().equals(event.getPlayer()))
            cli.setState(new TurnBeforeActionState());
        else
            cli.setState(new WaitingAfterTurnState());
    }
}
