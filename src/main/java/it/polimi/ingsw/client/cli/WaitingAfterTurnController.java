package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.common.events.mvevents.UpdateCurrentPlayer;

public class WaitingAfterTurnController extends CliController {
    @Override
    public void render() {
        if (!vm.isLastRound())
            cli.getOut().println("Please wait for other players to end their turn...");
        else
            cli.getOut().println("You have played your last turn. Waiting for others to finish...");
    }

    /* Other players' UpdateAction isn't a good indicator
       of the need a state change: it is not fired when a player disconnects.
       Therefore, UpdateCurrentPlayer is necessary. */
    @Override
    public void on(UpdateCurrentPlayer event) {
        super.on(event);
        
        cli.getOut().println();
        if (vm.isCurrentPlayer()) {
            cli.getOut().println(Cli.center("It's your turn."));
            cli.setController(new TurnBeforeActionController(), true);
        }
    }
}
