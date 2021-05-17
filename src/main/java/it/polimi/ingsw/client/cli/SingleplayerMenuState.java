package it.polimi.ingsw.client.cli;

import java.io.PrintStream;
import java.util.Scanner;

public class SingleplayerMenuState extends CliState {
    @Override
    public void render(Cli cli, PrintStream out, Scanner in) {
        Cli.clear(out);
        renderMainTitle(out);
        for (int i = 0; i < 2; i++)
            out.println();

        String nickname;

        do {
            nickname = Cli.prompt(out, in, "Nickname");
        } while (nickname.isBlank());

        out.println();
        out.printf("Welcome, %s!%n", nickname);
    }
}