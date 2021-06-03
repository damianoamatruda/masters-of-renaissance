package it.polimi.ingsw.client.cli.components;

import it.polimi.ingsw.client.cli.Cli;
import it.polimi.ingsw.client.cli.Renderable;
import it.polimi.ingsw.common.reducedmodel.ReducedDevCard;

import java.util.*;

public class DevSlots implements Renderable {
    private final Map<Integer, ReducedDevCard> slots;

    public DevSlots(Map<Integer, ReducedDevCard> slots) {
        this.slots = new HashMap<>(slots);
    }

    @Override
    public void render(Cli cli) {
        for (int i = 0; i < slots.size(); i += 4) {
            cli.trackSlimLine();
            List<List<String>> rows = new ArrayList<>();

            List<ReducedDevCard> cards = new ArrayList<>();
            for (int j = 0; j < 4 && j < slots.size() - i; j++) {
                cards.add(slots.get(i + j));
                rows.add(new ArrayList<>());
            }

            for (int j = 0; j < 4 && j < slots.size() - i; j++) {
                List<String> column = rows.get(j);
                ReducedDevCard card = cards.get(j);

                column.addAll(Arrays.asList(new DevelopmentCard(card).getString(cli).split("\n")));
            }

            String rowTemplate = "";
            for (int j = 0; j < 4 && j < slots.size() - i; j++) {
                rowTemplate += "%-38s │";
            }
            rowTemplate += "\n";

            int length = rows.stream().map(List::size).reduce(Integer::max).orElse(0);
            for (int k = 0; k < length; k++) {
                List<String> row = new ArrayList<>();
                for (int j = 0; j < 4 && j < slots.size() - i; j++) {
                    if (k < rows.get(j).size())
                        row.add(rows.get(j).get(k));
                    else row.add("");
                }
                cli.getOut().printf(rowTemplate, row.toArray());
            }
        }
        cli.trackSlimLine();
        cli.getOut().println("\n");
    }
}
