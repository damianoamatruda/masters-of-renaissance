package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.ReducedObjectPrinter;
import it.polimi.ingsw.common.events.mvevents.UpdateCurrentPlayer;
import it.polimi.ingsw.common.reducedmodel.ReducedGame;

import java.io.PrintStream;
import java.util.Scanner;

public class WaitingBeforeGameState extends CliState {
    private final int bookedSeats;

    public WaitingBeforeGameState(int bookedSeats) {
        this.bookedSeats = bookedSeats;
    }

    @Override
    public void render(Cli cli, PrintStream out, Scanner in, ReducedGame cache, ReducedObjectPrinter printer) {
        System.out.println("Waiting for a new game... " + bookedSeats + " player(s) joined");
    }

    @Override
    public void on(Cli cli, UpdateCurrentPlayer event) {
    }
}
