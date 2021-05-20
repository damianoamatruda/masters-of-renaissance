package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.common.events.vcevents.ReqEndTurn;
import it.polimi.ingsw.common.reducedmodel.ReducedGame;

import java.io.PrintStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class TurnAfterActionState extends CliTurnState {
    @Override
    public void render(Cli cli, PrintStream out, Scanner in, ReducedGame model) {
        Map<Character, Menu.Entry> entries = new LinkedHashMap<>();
        entries.put('L', new Menu.Entry("Leader action", (menu) -> leaderAction(cli, out, in)));
        entries.put('E', new Menu.Entry("End turn", (menu) -> endTurn(cli)));

        new Menu(entries).render(cli, out, in, model);
    }

    private void endTurn(Cli cli) {
        cli.sendToView(new ReqEndTurn());
    }
}
