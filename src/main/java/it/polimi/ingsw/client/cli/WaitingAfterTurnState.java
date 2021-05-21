package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.ReducedObjectPrinter;
import it.polimi.ingsw.common.reducedmodel.ReducedGame;

import java.io.PrintStream;
import java.util.Scanner;

public class WaitingAfterTurnState extends CliState {
    @Override
    public void render(Cli cli, PrintStream out, Scanner in, ReducedGame cache, ReducedObjectPrinter printer) {
        if(!cache.isLastRound())
            System.out.println("Please wait for other players to end their turn...");
        else
            System.out.println("You have played your last turn. Waiting for others to finish...");
    }
}
