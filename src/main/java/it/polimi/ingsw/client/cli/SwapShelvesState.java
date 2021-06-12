package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.common.events.mvevents.UpdateAction;
import it.polimi.ingsw.common.events.vcevents.ReqSwapShelves;

import static it.polimi.ingsw.client.cli.Cli.center;

public class SwapShelvesState extends CliState {
    private final CliState sourceState;
    private int shelfId1;
    private int shelfId2;

    public SwapShelvesState(CliState sourceState) {
        this.sourceState = sourceState;
    }

    @Override
    public void render(Cli cli) {
        cli.getOut().println();
        cli.getOut().println(center("~ Swap Shelves ~"));

        cli.getOut().println();
        cli.showShelves(cli.getViewModel().getLocalPlayerNickname());

        cli.getOut().println();
        promptFirstShelf(cli);
    }

    private void promptFirstShelf(Cli cli) {
        cli.promptInt("First shelf").ifPresentOrElse(shelfId1 -> {
            this.shelfId1 = shelfId1;
            promptSecondShelf(cli);
        }, () -> cli.setState(sourceState));
    }

    private void promptSecondShelf(Cli cli) {
        cli.promptInt("Second shelf").ifPresentOrElse(shelfId2 -> {
            this.shelfId2 = shelfId2;
            cli.dispatch(new ReqSwapShelves(this.shelfId1, this.shelfId2));
        }, () -> promptFirstShelf(cli));
    }

    @Override
    public void on(Cli cli, UpdateAction event) {
        cli.promptPause();
        cli.setState(sourceState);
    }
}
