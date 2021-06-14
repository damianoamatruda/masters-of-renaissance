package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.common.events.mvevents.UpdateAction;
import it.polimi.ingsw.common.events.vcevents.ReqSwapShelves;

import static it.polimi.ingsw.client.cli.Cli.center;

import it.polimi.ingsw.client.cli.components.ResourceContainers;
import it.polimi.ingsw.client.viewmodel.ViewModel;

public class SwapShelvesState extends CliController {
    private final CliController sourceState;
    private int shelfId1;
    private int shelfId2;

    public SwapShelvesState(CliController sourceState) {
        this.sourceState = sourceState;
    }

    @Override
    public void render() {
        ViewModel vm = cli.getViewModel();

        cli.getOut().println();
        cli.getOut().println(center("~ Swap Shelves ~"));

        cli.getOut().println();
        new ResourceContainers(
                vm.getLocalPlayerNickname(),
                vm.getPlayerWarehouseShelves(vm.getLocalPlayerNickname()),
                vm.getPlayerDepots(vm.getLocalPlayerNickname()),
                null)
                .render();

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
            cli.getUi().dispatch(new ReqSwapShelves(this.shelfId1, this.shelfId2));
        }, () -> promptFirstShelf(cli));
    }

    @Override
    public void on(UpdateAction event) {
        cli.promptPause();
        cli.setState(sourceState);
    }
}
