package it.polimi.ingsw.client.cli;

import java.io.PrintStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class TurnAfterActionState extends CliState {
    @Override
    public void render(Cli cli, PrintStream out, Scanner in) {
        Map<Character, Menu.Entry> entries = new LinkedHashMap<>();
        entries.put('L', new Menu.Entry("Leader action", (menu) -> leaderAction(cli, out, in)));
        entries.put('E', new Menu.Entry("End turn", (menu) -> endTurn(cli, out, in)));

        new Menu(entries).render(cli, out, in); // Will be eventually changed to sth different than a menu, no worries
    }

    private void leaderAction(Cli cli, PrintStream out, Scanner in) {
        //same implementation as before; should probably create a superclass TurnState with this method
    }

    private void endTurn(Cli cli, PrintStream out, Scanner in) {

    }
}
