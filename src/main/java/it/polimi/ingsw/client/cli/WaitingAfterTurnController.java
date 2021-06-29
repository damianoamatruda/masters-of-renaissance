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

    @Override
    public void on(UpdateCurrentPlayer event) {
        super.on(event);

        cli.getOut().println();
        if (event.getPlayer().equals(vm.getLocalPlayerNickname())) {
            cli.getOut().println(Cli.center("It's your turn."));
            cli.setController(new TurnBeforeActionController(), true);
        } else
            cli.getOut().println(Cli.center(String.format("Current player: %s", event.getPlayer())));
    }
}
