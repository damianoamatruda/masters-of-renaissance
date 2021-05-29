package it.polimi.ingsw.client.cli;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;

import it.polimi.ingsw.common.events.vcevents.ReqBuyDevCard;
import it.polimi.ingsw.common.reducedmodel.ReducedDevCard;
import it.polimi.ingsw.common.reducedmodel.ReducedDevCardGrid;

public class BuyDevelopmentCardState extends CliState {

    @Override
    public void render(Cli cli) {
        cli.getOut().println("Buying a development card.");

        ReducedDevCardGrid grid = cli.getViewModel().getDevCardGrid();
        cli.getPrinter().printCardGrid(grid);

        cli.getOut().println("\nChoose parameters:");
        //prompt for parameters
        final Map<Integer, Map<String, Integer>> shelves;
        int level = 0, slot = 0;

        String color = cli.prompt("Card color");
        boolean isNumber = false;
        while (!isNumber) {
            try {
                String input = cli.prompt("Card level");
                level = Integer.parseInt(input);
                isNumber = true;
            } catch (NumberFormatException e) {
                cli.getOut().println("Please input an integer.");
            }
        }

        isNumber = false;
        while (!isNumber) {
            try {
                String input = cli.prompt("Player board slot to assign to the card");
                slot = Integer.parseInt(input);
                isNumber = true;
            } catch (NumberFormatException e) {
                cli.getOut().println("Please input an integer.");
            }
        }

        int lev = level;
        ReducedDevCard card = grid.getGrid().get(color).stream()
                .filter(Objects::nonNull).map(Stack::peek)
                .map(id -> cli.getViewModel().getDevelopmentCard(id).orElse(null))
                .filter(Objects::nonNull)
                .filter(c -> c.getLevel() == lev).findAny().orElseThrow();
        Map<String, Integer> cost = new HashMap<>(card.getCost().getRequirements());

        cli.getOut().println("Resources need to be paid.");
        cli.getOut().println("Please specify how many resources to take from which container.");

        cli.getPrinter().showWarehouseShelves(cli.getViewModel().getLocalPlayerNickname());
        cli.getPrinter().showStrongbox(cli.getViewModel().getLocalPlayerNickname());

        shelves = cli.promptShelves(cost);

        //build request event
        cli.dispatch(new ReqBuyDevCard(color, level, slot, shelves));
    }
}
