package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.common.events.vcevents.ReqQuit;

public class GameEndController extends CliController {
    @Override
    public void render() {
        String prefix = vm.getWinnerPlayer() + " is the winner";
        if (vm.getWinnerPlayer().equals(vm.getLocalPlayer()))
            prefix = "You won";

        cli.getOut().println(prefix + " with " + vm.getWinnerPlayer().map(vm::getPlayerVictoryPoints).orElseThrow() + " points!");

        cli.getUi().dispatch(new ReqQuit());
        cli.promptPause();
    }
}
