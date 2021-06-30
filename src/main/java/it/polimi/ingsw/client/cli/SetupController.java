package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.common.events.mvevents.UpdateAction;
import it.polimi.ingsw.common.events.mvevents.UpdateCurrentPlayer;
import it.polimi.ingsw.common.events.mvevents.UpdateSetupDone;
import it.polimi.ingsw.common.events.mvevents.errors.ErrAction;
import it.polimi.ingsw.common.events.mvevents.errors.ErrInitialChoice;

public abstract class SetupController extends CliController {
    @Override
    public void on(ErrAction event) {
        /* If the data in the VM is correct setNextSetupState() could be used here as well.
           This different handler, which keeps track of the current player only,
           forces the client in a state that's compatible with the server's response,
           accepting it as a universal source of truth. */

        cli.getOut().println("Setup phase is concluded, advancing to game turns.");

        if (vm.isCurrentPlayer())
            cli.setController(new TurnBeforeActionController(), true);
        else
            cli.setController(new WaitingAfterTurnController(), true);
    }

    @Override
    public void on(ErrInitialChoice event) {
        if (event.isLeadersChoice()) // if the error is from the initial leaders choice
            if (event.getMissingLeadersCount() == 0) { // no leaders missing -> already chosen
                cli.getOut().println("Leader cards already chosen, advancing to next state.");
                setNextState();
            }
            else
                cli.reloadController(
                        String.format("Not enough leaders chosen: %d missing.", event.getMissingLeadersCount()));
        else {
            cli.getOut().println("Initial resources already chosen, advancing to next state.");
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

        setNextState();
    }
}
