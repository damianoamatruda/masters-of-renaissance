package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.common.events.vcevents.ReqChooseResources;

import java.util.Map;

public class SetupResourcesState extends CliState {
    private final int choosable;

    public SetupResourcesState(int choosable) {
        this.choosable = choosable;
    }

    @Override
    public void render(Cli cli) {
        cli.getOut().println("You have the right to " + choosable + " resources of choice. Which do you choose?");
        Map<Integer, Map<String, Integer>> shelves = cli.promptShelves();

        cli.dispatch(new ReqChooseResources(shelves));
    }
}
