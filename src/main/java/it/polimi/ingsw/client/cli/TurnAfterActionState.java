package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.cli.components.Menu;
import it.polimi.ingsw.common.events.mvevents.UpdateAction;
import it.polimi.ingsw.common.events.mvevents.UpdateCurrentPlayer;
import it.polimi.ingsw.common.events.vcevents.ReqEndTurn;

import java.util.LinkedHashMap;
import java.util.Map;

import static it.polimi.ingsw.client.cli.Cli.center;

public class TurnAfterActionState extends CliTurnState {
    @Override
    public void render(Cli cli) {
        cli.getOut().println();
        cli.getOut().println(center("~ It's your turn ~"));

        cli.getOut().println();
        Map<Character, Menu.Entry> entries = new LinkedHashMap<>();
        entries.put('L', new Menu.Entry("Leader Actions", cli1 -> cli1.setState(new LeaderActionsState(this))));
        entries.put('S', new Menu.Entry("Swap Shelves", cli1 -> cli1.setState(new SwapShelvesState(this))));
        entries.put('E', new Menu.Entry("End Turn", this::endTurn));
        entries.put('Q', new Menu.Entry("Quit to Title", this::quitToTitle));
        new Menu(entries, this::quitToTitle).render(cli);
    }

    private void endTurn(Cli cli) {
        cli.getUi().dispatch(new ReqEndTurn());
    }

    @Override
    public void on(Cli cli, UpdateAction event) {
        cli.promptPause();
        if (event.getAction().equals(UpdateAction.ActionType.END_TURN))
            cli.setState(new WaitingAfterTurnState());
        else
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
