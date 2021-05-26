package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.common.events.vcevents.ReqQuit;

public class GameEndState extends CliState {
    @Override
    public void render(Cli cli) {
        if (cli.getViewModel().getWinner().equals(cli.getViewModel().getNickname()))
            cli.getOut().println("You won with " + cli.getViewModel().getVictoryPoints(cli.getViewModel().getWinner()) + " points! CONGRATULATIONS!");
        else
            cli.getOut().println(cli.getViewModel().getWinner() + " is the winner with " + cli.getViewModel().getVictoryPoints(cli.getViewModel().getWinner()) + " points. Better luck next time!");

        cli.dispatch(new ReqQuit());

        // show game end message for a little longer
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
