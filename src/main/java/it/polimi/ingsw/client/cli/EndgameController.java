package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.cli.components.Leaderboard;
import it.polimi.ingsw.common.events.vcevents.ReqQuit;

public class EndgameController extends CliController {
    @Override
    public void render() {
        cli.getOut().println();
        new Leaderboard().render();

        vm.getWinnerPlayer().map(vm::getPlayerVictoryPoints).ifPresentOrElse(points -> {
            if (vm.getWinnerPlayer().equals(vm.getLocalPlayer()))
                cli.alert(String.format("You won with %d points! CONGRATULATIONS!", points));
            else
                cli.alert(String.format("%s is the winner with %d points!", vm.getWinnerPlayer().get(), points));
        }, () -> cli.alert("Lorenzo il Magnifico has won. Better luck next time!"));

        cli.getUi().dispatch(new ReqQuit());
    }
}
