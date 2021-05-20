package it.polimi.ingsw.client.cli;

import java.io.PrintStream;
import java.util.Scanner;

import it.polimi.ingsw.common.reducedmodel.ReducedGame;

public class WaitingBeforeGameState extends CliState {
    private int bookedSeats;

    public WaitingBeforeGameState(int bookedSeats) {
        this.bookedSeats = bookedSeats;
    }

    @Override
    public void render(Cli cli, PrintStream out, Scanner in, ReducedGame model) {
        System.out.println("Waiting for a new game... " + bookedSeats + " player(s) joined");
    }
}