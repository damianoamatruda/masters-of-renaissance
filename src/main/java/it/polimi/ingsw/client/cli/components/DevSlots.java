package it.polimi.ingsw.client.cli.components;

import it.polimi.ingsw.client.cli.Cli;
import it.polimi.ingsw.common.reducedmodel.ReducedDevCard;

import java.util.ArrayList;
import java.util.List;

public class DevSlots extends StringComponent {
    private final List<ReducedDevCard> slots;

    public DevSlots(List<ReducedDevCard> slots) {
        this.slots = new ArrayList<>(slots);
    }

    @Override
    public String getString(Cli cli) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < slots.size(); i += 4) {
            // List<List<String>> rows = new ArrayList<>();

            List<ReducedDevCard> cards = new ArrayList<>();
            for (int j = 0; j < 4 && j < slots.size() - i; j++) {
                cards.add(slots.get(i + j));
            }

            List<DevelopmentCard> devCardComponents = new ArrayList<>();
            for (int j = 0; j < 4 && j < slots.size() - i; j++)
                devCardComponents.add(new DevelopmentCard(cards.get(j)));

            int maxWidth = devCardComponents.stream().map(c -> c.getString(cli)).mapToInt(Cli::maxLineWidth).max().orElse(0);
            int maxHeight = Cli.maxLinesHeight(devCardComponents.stream().map(c -> c.getString(cli)).toList());
            
            List<List<String>> rows = new ArrayList<>();
            for (DevelopmentCard devCardComponent : devCardComponents)
                rows.add(new Box(devCardComponent, -1, maxWidth, maxHeight).getString(cli).lines().toList());
                
            int length = rows.stream().mapToInt(List::size).max().orElse(0);
            for (int k = 0; k < length; k++) {
                for (List<String> row : rows) {
                    if (k < row.size())
                        stringBuilder.append(row.get(k)).append(" ");
                    else
                        stringBuilder.append("".repeat(maxWidth + 1));
                }
                stringBuilder.append("\n");
            }
        }

        return Cli.center(stringBuilder.toString());
    }
}
