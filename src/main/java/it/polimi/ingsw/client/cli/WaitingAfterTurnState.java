package it.polimi.ingsw.client.cli;

public class WaitingAfterTurnState extends CliController {
    @Override
    public void render() {
        if (!vm.isLastRound())
            cli.getOut().println("Please wait for other players to end their turn...");
        else
            cli.getOut().println("You have played your last turn. Waiting for others to finish...");
    }
}
