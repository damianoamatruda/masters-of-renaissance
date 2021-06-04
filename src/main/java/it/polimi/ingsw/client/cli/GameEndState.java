package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.common.events.vcevents.ReqQuit;

public class GameEndState extends CliState {
    @Override
    public void render(Cli cli) {
        if (cli.getViewModel().getWinner().equals(cli.getViewModel().getLocalPlayerNickname()))
            cli.getOut().println("You won with " + cli.getViewModel().getPlayerData(cli.getViewModel().getWinner()) + " points! CONGRATULATIONS!");
        else
            cli.getOut().println(cli.getViewModel().getWinner() + " is the winner with " + cli.getViewModel().getPlayerData(cli.getViewModel().getWinner()).getVictoryPoints() + " points!");

        cli.dispatch(new ReqQuit());
        cli.promptPause();
    }
}
