package it.polimi.ingsw.client.cli;

import java.io.PrintStream;
import java.util.Scanner;

public class WaitingState extends CliState {
    private int bookedSeats;

    public WaitingState(int bookedSeats) {
        this.bookedSeats = bookedSeats;
    }

    @Override
    public void render(Cli cli, PrintStream out, Scanner in) {
        System.out.println("Waiting for a new game..." + bookedSeats + " joined");
    }
}
