package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.common.events.mvevents.UpdateCurrentPlayer;

public class WaitingBeforeGameState extends CliState {
    private final int bookedSeats;

    public WaitingBeforeGameState(int bookedSeats) {
        this.bookedSeats = bookedSeats;
    }

    @Override
    public void render(Cli cli) {
        cli.getOut().println("Waiting for a new game... " + bookedSeats + " player(s) joined");
    }

    @Override
    public void on(Cli cli, UpdateCurrentPlayer event) {
    }
}
