package it.polimi.ingsw.client.cli;

import java.io.PrintStream;
import java.util.Scanner;

public class InputPlayersCountState extends CliState {

    @Override
    public void render(Cli cli, PrintStream out, Scanner in) {
        Cli.prompt(out, in, "You are the first player. Choose players count");
        //send
        //if goes wrong, this state will be repeated
    }
}
