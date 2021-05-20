package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.common.reducedmodel.ReducedGame;

import java.io.PrintStream;
import java.util.Scanner;

public class WaitingForTurnState extends CliState {
    @Override
    public void render(Cli cli, PrintStream out, Scanner in, ReducedGame model) {
        System.out.println("Please wait for other players to end their turn...");
    }
}
