package it.polimi.ingsw.client.cli;

import java.io.PrintStream;
import java.util.Scanner;

public abstract class CliState implements Renderable {
    protected static void renderMainTitle(PrintStream out) {
        out.print(Cli.center(Cli.convertStreamToString(CliState.class.getResourceAsStream("/assets/cli/title.txt"))));
    }

    @Override
    public abstract void render(Cli cli, PrintStream out, Scanner in);
}
