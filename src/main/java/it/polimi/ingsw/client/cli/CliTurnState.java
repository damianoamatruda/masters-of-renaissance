package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.common.events.vcevents.ReqQuit;

public abstract class CliTurnState extends CliState {
    protected void quitToTitle(Cli cli) {
        cli.getUi().dispatch(new ReqQuit());
    }
}
