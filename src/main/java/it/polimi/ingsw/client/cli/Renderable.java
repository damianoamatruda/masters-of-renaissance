package it.polimi.ingsw.client.cli;

import java.io.PrintStream;
import java.util.Scanner;

@FunctionalInterface
public interface Renderable {
    void render(Cli cli, PrintStream out, Scanner in);
}
