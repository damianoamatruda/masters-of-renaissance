package it.polimi.ingsw.client.cli;

import java.io.PrintStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

import it.polimi.ingsw.client.ReducedObjectPrinter;
import it.polimi.ingsw.common.reducedmodel.ReducedGame;

public class MainMenuState extends CliState {
    @Override
    public void render(Cli cli, PrintStream out, Scanner in, ReducedGame cache, ReducedObjectPrinter printer) {
        Cli.clear(out);
        renderMainTitle(out);
        for (int i = 0; i < 2; i++)
            out.println();
        renderMainMenu(cli, out, in, cache, printer);
    }

    private void renderMainMenu(Cli cli, PrintStream out, Scanner in, ReducedGame cache, ReducedObjectPrinter printer) {
        Map<Character, Menu.Entry> entries = new LinkedHashMap<>();
        entries.put('S', new Menu.Entry("Singleplayer", (menu) -> cli.setState(new SingleplayerMenuState())));
        entries.put('M', new Menu.Entry("Multiplayer", (menu) -> cli.setState(new MultiplayerMenuState())));
        entries.put('O', new Menu.Entry("Options...", (menu) -> out.println("Options")));
        entries.put('Q', new Menu.Entry("Quit Game", (menu) -> cli.quit()));
        new Menu(entries).render(cli, out, in, cache, printer);
    }
}
