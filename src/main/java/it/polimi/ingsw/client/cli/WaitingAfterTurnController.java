package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.common.events.mvevents.UpdateCurrentPlayer;

import static it.polimi.ingsw.client.cli.Cli.center;

public class WaitingAfterTurnController extends CliController {
    @Override
    public void render() {
        if (!vm.isLastRound()) {
            cli.getOut().println();
            cli.getOut().println(center("Waiting for other players to end their turn..."));
        } else {
            cli.getOut().println();
            cli.getOut().println(center("You have played your last turn. Waiting for other players to finish..."));
        }
    }

    /* Other players' UpdateAction is not a good indicator
       of the need a state change: it is not fired when a player disconnects.
       Therefore, UpdateCurrentPlayer is necessary. */
    @Override
    public void on(UpdateCurrentPlayer event) {
        super.on(event);

        if (vm.localPlayerIsCurrent()) {
            cli.alert("It's your turn.");
            cli.setController(new TurnBeforeActionController());
        }
    }
}
