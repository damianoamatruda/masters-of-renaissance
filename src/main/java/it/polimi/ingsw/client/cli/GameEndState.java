package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.common.events.vcevents.ReqQuit;

public class GameEndState extends CliState {
    @Override
    public void render(Cli cli) {
        if (cli.getCache().getWinner().equals(cli.getCache().getNickname()))
            cli.getOut().println("You won with " + cli.getCache().getVictoryPoints(cli.getCache().getWinner()) + " points! CONGRATULATIONS!");
        else
            cli.getOut().println(cli.getCache().getWinner() + " is the winner with " + cli.getCache().getVictoryPoints(cli.getCache().getWinner()) + " points. Better luck next time!");

        cli.dispatch(new ReqQuit());

        // show game end message for a little longer
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
