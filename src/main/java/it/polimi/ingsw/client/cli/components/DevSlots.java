package it.polimi.ingsw.client.cli.components;

import it.polimi.ingsw.client.cli.Cli;
import it.polimi.ingsw.common.reducedmodel.ReducedDevCard;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static it.polimi.ingsw.client.cli.Cli.center;
import static it.polimi.ingsw.client.cli.Cli.maxLinesHeight;

/** Cli component that gives a string representation of a development card player slot. */
public class DevSlots extends StringComponent {
    private final List<Optional<ReducedDevCard>> slots;

    public DevSlots(List<Optional<ReducedDevCard>> slots) {
        this.slots = new ArrayList<>(slots);
    }

    @Override
    public String getString() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(String.format("%s's development card slots:\n", Cli.getInstance().getViewModel().getLocalPlayer().get()));

        for (int i = 0; i < slots.size(); i += 4) {
            List<Optional<ReducedDevCard>> cards = new ArrayList<>();
            for (int j = 0; j < 4 && j < slots.size() - i; j++) {
                cards.add(slots.get(i + j));
            }

            List<Optional<DevelopmentCard>> devCardComponents = new ArrayList<>();
            for (int j = 0; j < 4 && j < slots.size() - i; j++)
                devCardComponents.add(cards.get(j).map(DevelopmentCard::new));

            int maxWidth = devCardComponents.stream().map(oc -> oc.map(DevelopmentCard::getString).orElse("")).mapToInt(Cli::maxLineWidth).max().orElse(0);
            int maxHeight = maxLinesHeight(devCardComponents.stream().map(oc -> oc.map(DevelopmentCard::getString).orElse("")).toList());

            List<List<String>> rows = new ArrayList<>();
            for (Optional<DevelopmentCard> optionalDevCardComponent : devCardComponents)
                optionalDevCardComponent.ifPresentOrElse(
                    devCardComponent -> rows.add(new Box(devCardComponent, -1, maxWidth, maxHeight).getString().lines().toList()),
                    () -> rows.add(new Box(null).getString().lines().toList()));

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

        return center(stringBuilder.toString());
    }
}
