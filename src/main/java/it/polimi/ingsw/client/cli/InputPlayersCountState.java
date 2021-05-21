package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.ReducedObjectPrinter;
import it.polimi.ingsw.common.events.vcevents.ReqNewGame;
import it.polimi.ingsw.common.reducedmodel.ReducedGame;

import java.io.PrintStream;
import java.util.Scanner;

public class InputPlayersCountState extends CliState {

    @Override
    public void render(Cli cli, PrintStream out, Scanner in, ReducedGame cache, ReducedObjectPrinter printer) {
        int count = 0;
        boolean isNumber = false;
        while (!isNumber) {
            try {
                String input = cli.prompt(out, in, "You are the first player. Choose players count");
                count = Integer.parseInt(input);
                isNumber = true;
            } catch (NumberFormatException e) {
                System.out.println("Please input an integer.");
            }
        }

        cli.sendToView(new ReqNewGame(count));
        //send
        //if goes wrong, this state will be repeated
    }
}
