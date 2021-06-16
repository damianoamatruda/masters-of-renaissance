package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.common.events.mvevents.UpdateAction;
import it.polimi.ingsw.common.events.mvevents.UpdateCurrentPlayer;
import it.polimi.ingsw.common.events.mvevents.UpdateSetupDone;
import it.polimi.ingsw.common.events.mvevents.errors.ErrAction;
import it.polimi.ingsw.common.events.mvevents.errors.ErrInitialChoice;

public abstract class SetupState extends CliController {
    @Override
    public void on(ErrAction event) {
        /* If the data in the VM is correct setNextSetupState() could be used here as well.
           This different handler, which keeps track of the current player only,
           forces the client in a state that's compatible with the server's response,
           accepting it as a universal source of truth. */
        if (vm.getCurrentPlayer().equals(vm.getLocalPlayerNickname()))
            cli.setController(new TurnBeforeActionState());
        else
            cli.setController(new WaitingAfterTurnState());
    }

    @Override
    public void on(ErrInitialChoice event) {
        // repeats either SetupLeadersState or SetupResourcesState
        cli.reloadController(event.isLeadersChoice() ? // if the error is from the initial leaders choice
                event.getMissingLeadersCount() == 0 ?
                        "Leaders already chosen" :        // if the count is zero it means the leaders were already chosen
                        String.format("Not enough leaders chosen: %d missing.", event.getMissingLeadersCount()) :
                "Resources already chosen");          // else it's from the resources choice
    }

    @Override
    public void on(UpdateAction event) {
        if (!event.getPlayer().equals(vm.getLocalPlayerNickname()))
            return;

        setNextSetupState();
    }

    @Override
    public void on(UpdateCurrentPlayer event) {
        super.on(event);

        setNextSetupState();
    }

    @Override
    public void on(UpdateSetupDone event) {
        super.on(event);

        setNextSetupState();
    }
}
