package it.polimi.ingsw.client.cli;

import java.io.PrintStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class TurnBeforeActionState extends CliState {
    @Override
    public void render(Cli cli, PrintStream out, Scanner in) {
        Map<Character, Menu.Entry> entries = new LinkedHashMap<>();
        entries.put('1', new Menu.Entry("Buy a card", (menu) -> buyCard(cli, out, in)));
        entries.put('2', new Menu.Entry("Obtain resources", (menu) -> getResources(cli, out, in)));
        entries.put('3', new Menu.Entry("Activate production", (menu) -> produce(cli, out, in)));
        entries.put('L', new Menu.Entry("Leader action", (menu) -> leaderAction(cli, out, in)));

        new Menu(entries).render(cli, out, in); // Will be eventually changed to sth different than a menu, no worries

    }

    private void buyCard(Cli cli, PrintStream out, Scanner in) {
        //prompt for parameters
        //build request event
    }

    private void getResources(Cli cli, PrintStream out, Scanner in) {
        //same
    }

    private void produce(Cli cli, PrintStream out, Scanner in) {

    }

    private void leaderAction(Cli cli, PrintStream out, Scanner in) {

    }
}
