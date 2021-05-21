package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.ReducedObjectPrinter;
import it.polimi.ingsw.common.events.vcevents.ReqQuit;
import it.polimi.ingsw.common.reducedmodel.ReducedGame;

import java.io.PrintStream;
import java.util.Scanner;

public class GameEndState extends CliState {
    @Override
    public void render(Cli cli, PrintStream out, Scanner in, ReducedGame cache, ReducedObjectPrinter printer) {
        if(cache.getWinner().equals(cache.getNickname()))
            System.out.println("You won with " + cache.getVictoryPoints(cache.getWinner()) + " points! CONGRATULATIONS!");
        else 
            System.out.println(cache.getWinner() + " is the winner with " + cache.getVictoryPoints(cache.getWinner()) + " points. Better luck next time!");
        
        cli.dispatch(new ReqQuit());
        
        // show game end message for a little longer
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
