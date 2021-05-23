package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.cli.components.Menu;
import it.polimi.ingsw.common.events.vcevents.ReqEndTurn;

import java.util.LinkedHashMap;
import java.util.Map;

public class TurnAfterActionState extends CliTurnState {
    @Override
    public void render(Cli cli) {
        Map<Character, Menu.Entry> entries = new LinkedHashMap<>();
        entries.put('L', new Menu.Entry("Leader action", this::leaderAction));
        entries.put('E', new Menu.Entry("End turn", this::endTurn));

        new Menu(entries).render(cli);
    }

    private void endTurn(Cli cli) {
        cli.dispatch(new ReqEndTurn());
    }
}
