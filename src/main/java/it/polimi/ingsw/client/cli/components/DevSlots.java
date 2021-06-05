package it.polimi.ingsw.client.cli.components;

import it.polimi.ingsw.client.cli.Cli;
import it.polimi.ingsw.client.cli.Renderable;
import it.polimi.ingsw.common.reducedmodel.ReducedDevCard;

import java.util.*;

public class DevSlots extends StringComponent {
    private final Map<Integer, ReducedDevCard> slots;
    private final static int cellWidth = 30;

    public DevSlots(Map<Integer, ReducedDevCard> slots) {
        this.slots = new HashMap<>(slots);
    }

    @Override
    public String getString(Cli cli) {
        StringBuilder output = new StringBuilder();

        for (int i = 0; i < slots.size(); i += 4) {
            output.append(Cli.slimLine(cellWidth * Integer.min(4, slots.size()) + 1));
            List<List<String>> rows = new ArrayList<>();

            List<ReducedDevCard> cards = new ArrayList<>();
            for (int j = 0; j < 4 && j < slots.size() - i; j++) {
                cards.add(slots.get(i + j));
                rows.add(new ArrayList<>());
            }

            for (int j = 0; j < 4 && j < slots.size() - i; j++)
                rows.get(j).addAll(Arrays.asList(new DevelopmentCard(cards.get(j)).getString(cli).split("\\R")));

            int length = rows.stream().map(List::size).reduce(Integer::max).orElse(0);
            for (int k = 0; k < length; k++) {
                for (int j = 0; j < 4 && j < slots.size() - i; j++) {
                    if (k < rows.get(j).size())
                        output.append(Cli.left(rows.get(j).get(k), 38)).append(" │");
                }
                output.append("\n");
            }
        }
        output.append(Cli.slimLine(cellWidth * Integer.min(4, slots.size()) + 1)).append("\n\n");

        return output.toString();
    }
}
