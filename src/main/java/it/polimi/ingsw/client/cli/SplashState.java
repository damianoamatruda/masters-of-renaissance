package it.polimi.ingsw.client.cli;

import java.io.PrintStream;
import java.util.Scanner;

import it.polimi.ingsw.common.reducedmodel.ReducedGame;

public class SplashState extends CliState {
    private static void renderCredits(PrintStream out) {
        out.print(Cli.center(Cli.convertStreamToString(CliState.class.getResourceAsStream("/assets/cli/credits.txt"))));
    }

    private static void renderCastle(PrintStream out) {
        out.print(Cli.center(Cli.convertStreamToString(CliState.class.getResourceAsStream("/assets/cli/castle.txt"))));
    }

    private static void pausePressEnter(PrintStream out, Scanner in) {
        out.print(Cli.center("[Press ENTER]"));
        Cli.pause(in);
    }

    @Override
    public void render(Cli cli, PrintStream out, Scanner in, ReducedGame model) {
        Cli.clear(out);
        renderMainTitle(out);
        for (int i = 0; i < 2; i++)
            out.println();
        renderCredits(out);
        out.println();
        renderCastle(out);
        out.println();
        pausePressEnter(out, in);
        Cli.clear(out);
        cli.setState(new MainMenuState());
    }
}
