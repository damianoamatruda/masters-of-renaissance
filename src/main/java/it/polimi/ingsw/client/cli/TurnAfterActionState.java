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
    public void render() {
        cli.getOut().println();
        cli.getOut().println(center("~ It's your turn ~"));

        cli.getOut().println();
        Map<Character, Menu.Entry> entries = new LinkedHashMap<>();
        entries.put('L', new Menu.Entry("Leader Actions", cli1 -> cli1.setController(new LeaderActionsState(this))));
        entries.put('S', new Menu.Entry("Swap Shelves", cli1 -> cli1.setController(new SwapShelvesState(this))));
        entries.put('E', new Menu.Entry("End Turn", this::endTurn));
        entries.put('Q', new Menu.Entry("Quit to Title", this::quitToTitle));
        new Menu(entries, this::quitToTitle).render();
    }

    private void endTurn(Cli cli) {
        cli.getUi().dispatch(new ReqEndTurn());
    }

    @Override
    public void on(UpdateAction event) {
        new Thread(() -> {
            cli.promptPause();
            if (event.getAction().equals(UpdateAction.ActionType.END_TURN))
                cli.setController(new WaitingAfterTurnState());
            else
                cli.setController(new TurnAfterActionState());
        }).start();
    }

    @Override
    public void on(UpdateCurrentPlayer event) {
        super.on(event);
        if (vm.getLocalPlayerNickname().equals(event.getPlayer()))
            cli.setController(new TurnBeforeActionState());
        else
            cli.setController(new WaitingAfterTurnState());
    }
}
