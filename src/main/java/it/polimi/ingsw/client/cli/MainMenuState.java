package it.polimi.ingsw.client.cli;

import java.io.PrintStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class MainMenuState extends CliState {
    @Override
    public void render(Cli cli, PrintStream out, Scanner in) {
        Cli.clear(out);
        renderMainTitle(out);
        for (int i = 0; i < 2; i++)
            out.println();
        renderMainMenu(cli, out, in);
    }

    private void renderMainMenu(Cli cli, PrintStream out, Scanner in) {
        Map<Character, Menu.Entry> entries = new LinkedHashMap<>();
        entries.put('S', new Menu.Entry("Singleplayer", (menu) -> out.println("Solo")));
        entries.put('M', new Menu.Entry("Multiplayer", (menu) -> out.println("Multi")));
        entries.put('O', new Menu.Entry("Options...", (menu) -> out.println("Options")));
        entries.put('Q', new Menu.Entry("Quit Game", (menu) -> {
            Cli.clear(out);
            out.print("Quitting in 2 seconds...");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            Cli.clear(out);
            out.print("Quitting in 1 seconds...");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            Cli.clear(out);
        }));
        new Menu(entries).render(cli, out, in);
    }
}
