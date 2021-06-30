package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.common.events.vcevents.ReqQuit;

public class GameEndController extends CliController {
    @Override
    public void render() {
        String prefix = vm.getWinnerPlayer() + " is the winner";
        if (vm.getWinnerPlayer().equals(vm.getLocalPlayer()))
            prefix = "You won";

        cli.getOut().println(prefix + " with " + vm.getPlayerVictoryPoints(vm.getWinnerPlayer()) + " points!");

        cli.getUi().dispatch(new ReqQuit());
        cli.promptPause();
    }
}
