package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.common.events.vcevents.ReqChooseResources;
import it.polimi.ingsw.common.reducedmodel.ReducedGame;

import java.io.PrintStream;
import java.util.Map;
import java.util.Scanner;

public class SetupResourcesState extends CliState {
    private final int choosable;

    public SetupResourcesState(int choosable) {
        this.choosable = choosable;
    }
    @Override
    public void render(Cli cli, PrintStream out, Scanner in, ReducedGame cache) {
        System.out.println("You have the right to " + choosable + " resources of choice. Which do you choose?");
        Map<Integer, Map<String, Integer>> shelves = cli.promptShelves(out, in);

        cli.sendToView(new ReqChooseResources(shelves));
    }
}
