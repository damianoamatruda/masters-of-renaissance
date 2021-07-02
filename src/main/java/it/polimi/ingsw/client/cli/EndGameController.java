package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.cli.components.LeaderBoard;
import it.polimi.ingsw.common.events.vcevents.ReqQuit;

public class EndGameController extends CliController {
    @Override
    public void render() {
        String prefix = vm.getWinnerPlayer().orElse("Lorenzo il Magnifico") + " is the winner";
        if (vm.getWinnerPlayer().equals(vm.getLocalPlayer()))
            prefix = "You won";

        cli.alert(prefix + " with " + vm.getWinnerPlayer().map(vm::getPlayerVictoryPoints).orElse(vm.getBlackPoints()) + " points!");

        new LeaderBoard().render();

        cli.getUi().dispatch(new ReqQuit());
    }
}
