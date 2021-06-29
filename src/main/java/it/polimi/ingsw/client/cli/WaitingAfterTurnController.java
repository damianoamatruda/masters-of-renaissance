package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.common.events.mvevents.UpdateAction;
import it.polimi.ingsw.common.events.mvevents.UpdateCurrentPlayer;
import it.polimi.ingsw.common.events.mvevents.UpdateAction.ActionType;

public class WaitingAfterTurnController extends CliController {
    @Override
    public void render() {
        if (!vm.isLastRound())
            cli.getOut().println("Please wait for other players to end their turn...");
        else
            cli.getOut().println("You have played your last turn. Waiting for others to finish...");
    }

    @Override
    public void on(UpdateAction event) {
        super.on(event);
        
        cli.getOut().println();
        if (event.getAction() == ActionType.END_TURN &&
            !event.getPlayer().equals(vm.getLocalPlayerNickname()) &&
            vm.getCurrentPlayer().equals(vm.getLocalPlayerNickname())) {
            cli.getOut().println(Cli.center("It's your turn."));
            cli.setController(new TurnBeforeActionController(), true);
        }
    }
}
