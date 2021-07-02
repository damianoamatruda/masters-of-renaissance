package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.cli.components.LeaderBoard;
import it.polimi.ingsw.common.events.vcevents.ReqQuit;

public class EndGameController extends CliController {
    @Override
    public void render() {
        vm.getWinnerPlayer().map(vm::getPlayerVictoryPoints).ifPresentOrElse(points -> {
                String prefix = vm.getWinnerPlayer().get() + " is the winner";
                if (vm.getWinnerPlayer().equals(vm.getLocalPlayer()))
                    prefix = "You won";

                cli.alert(prefix + " with " + points + " points!");
            }, () -> cli.alert("Lorenzo il Magnifico won, better luck next time!"));

        new LeaderBoard().render();

        cli.getUi().dispatch(new ReqQuit());
    }
}
