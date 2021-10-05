package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.cli.components.ResourceContainerSet;
import it.polimi.ingsw.common.events.mvevents.UpdateAction;
import it.polimi.ingsw.common.events.vcevents.ReqSwapShelves;

import static it.polimi.ingsw.client.cli.Cli.center;

public class SwapShelvesController extends CliController {
    private final CliController sourceController;
    private int shelfId1;
    private int shelfId2;

    public SwapShelvesController(CliController sourceController) {
        this.sourceController = sourceController;
    }

    @Override
    public void render() {
        cli.getOut().println();
        cli.getOut().println(center("~ Swap Shelves ~"));

        cli.getOut().println();
        new ResourceContainerSet(
                vm.getLocalPlayer().orElseThrow(),
                vm.getLocalPlayer().map(vm::getPlayerWarehouseShelves).orElseThrow(),
                vm.getLocalPlayer().map(vm::getPlayerDepots).orElseThrow(),
                null)
                .render();

        cli.getOut().println();
        promptFirstShelf();
    }

    private void promptFirstShelf() {
        cli.promptInt("First shelf").ifPresentOrElse(shelfId1 -> {
            this.shelfId1 = shelfId1;
            promptSecondShelf();
        }, () -> cli.setController(sourceController));
    }

    private void promptSecondShelf() {
        cli.promptInt("Second shelf").ifPresentOrElse(shelfId2 -> {
            this.shelfId2 = shelfId2;
            cli.getUi().dispatch(new ReqSwapShelves(this.shelfId1, this.shelfId2));
        }, this::promptFirstShelf);
    }

    @Override
    public void on(UpdateAction event) {
        cli.promptPause();
        cli.setController(sourceController);
    }
}
