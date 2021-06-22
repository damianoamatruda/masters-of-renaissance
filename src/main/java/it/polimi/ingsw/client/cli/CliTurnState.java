package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.common.events.mvevents.errors.ErrActiveLeaderDiscarded;
import it.polimi.ingsw.common.events.vcevents.ReqQuit;

public abstract class CliTurnState extends CliController {
    protected void quitToTitle(Cli cli) {
        cli.getUi().dispatch(new ReqQuit());
    }

    @Override
    public void on(ErrActiveLeaderDiscarded event) {
        super.on(event);

        cli.reloadController("Active leader cannot be discarded.");
    }
}
