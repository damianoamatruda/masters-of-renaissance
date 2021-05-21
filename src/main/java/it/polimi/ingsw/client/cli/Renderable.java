package it.polimi.ingsw.client.cli;

import java.io.PrintStream;
import java.util.Scanner;

import it.polimi.ingsw.client.ReducedObjectPrinter;
import it.polimi.ingsw.common.reducedmodel.ReducedGame;

@FunctionalInterface
public interface Renderable {
    void render(Cli cli, PrintStream out, Scanner in, ReducedGame cache, ReducedObjectPrinter printer);
}
