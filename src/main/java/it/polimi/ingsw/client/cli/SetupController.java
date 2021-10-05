package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.common.events.mvevents.UpdateAction;
import it.polimi.ingsw.common.events.mvevents.UpdateCurrentPlayer;
import it.polimi.ingsw.common.events.mvevents.UpdateSetupDone;
import it.polimi.ingsw.common.events.mvevents.errors.ErrAction;
import it.polimi.ingsw.common.events.mvevents.errors.ErrInitialChoice;

import static it.polimi.ingsw.client.cli.Cli.center;

public abstract class SetupController extends CliController {
    @Override
    public void on(ErrAction event) {
        /* If the data in the VM is correct setNextSetupState() could be used here as well.
           This different handler, which keeps track of the current player only,
           forces the client in a state that is compatible with the server's response,
           accepting it as a universal source of truth. */

        cli.alert("Setup phase is concluded, advancing to game turns.");

        if (vm.localPlayerIsCurrent())
            cli.setController(new TurnBeforeActionController());
        else
            cli.setController(new WaitingAfterTurnController());
    }

    @Override
    public void on(ErrInitialChoice event) {
        if (event.isLeadersChoice()) // If the error is from the initial leaders choice
            if (event.getMissingLeadersCount() == 0) { // No leaders missing -> already chosen
                cli.alert("Leader cards already chosen, advancing to next state.");
                setNextState();
            } else
                cli.reloadController(
                        String.format("Not enough leaders chosen: %d missing.", event.getMissingLeadersCount()));
        else {
            cli.alert("Initial resources already chosen, advancing to next state.");
            setNextState();
        }
    }

    @Override
    public void on(UpdateAction event) {
        if (vm.getLocalPlayer().isPresent() && !event.getPlayer().equals(vm.getLocalPlayer().get()))
            return;

        setNextState();
    }

    @Override
    public void on(UpdateCurrentPlayer event) {
        super.on(event);

        setNextState();
    }

    @Override
    public void on(UpdateSetupDone event) {
        super.on(event);

        if (vm.getPlayers().size() > 1) {
            cli.getOut().println();
            cli.getOut().println(center("All players have finished their setup!"));
        }

        setNextState();
    }
}
