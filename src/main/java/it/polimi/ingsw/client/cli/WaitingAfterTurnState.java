package it.polimi.ingsw.client.cli;

public class WaitingAfterTurnState extends CliState {
    @Override
    public void render(Cli cli) {
        if (!cli.getCache().isLastRound())
            cli.getOut().println("Please wait for other players to end their turn...");
        else
            cli.getOut().println("You have played your last turn. Waiting for others to finish...");
    }
}
