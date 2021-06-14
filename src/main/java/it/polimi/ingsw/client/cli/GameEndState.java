package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.viewmodel.ViewModel;
import it.polimi.ingsw.common.events.vcevents.ReqQuit;

public class GameEndState extends CliController {
    @Override
    public void render() {
        Cli cli = Cli.getInstance();
        ViewModel vm = cli.getViewModel();

        String prefix = vm.getWinner() + " is the winner";
        if (vm.getWinner().equals(vm.getLocalPlayerNickname()))
            prefix = "You won";

        cli.getOut().println(prefix + " with " + vm.getPlayerVictoryPoints(vm.getWinner()) + " points!");

        cli.getUi().dispatch(new ReqQuit());
        cli.promptPause();
    }
}
