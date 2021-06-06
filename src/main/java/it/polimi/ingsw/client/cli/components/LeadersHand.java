package it.polimi.ingsw.client.cli.components;

import it.polimi.ingsw.client.cli.Cli;
import it.polimi.ingsw.common.reducedmodel.ReducedLeaderCard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LeadersHand extends StringComponent {
    private final static int cellWidth = 34;
    private final List<ReducedLeaderCard> leaders;

    public LeadersHand(List<ReducedLeaderCard> leaders) {
        this.leaders = leaders;
    }

    @Override
    public String getString(Cli cli) {
        StringBuilder stringBuilder = new StringBuilder();

        int i;
        for (i = 0; i < leaders.size(); i += 4) {
            stringBuilder.append(Cli.slimLine(cellWidth * (leaders.size() % 5) + 1));

            List<ReducedLeaderCard> cards = new ArrayList<>();
            for (int j = 0; j < 4 && j < leaders.size() - i; j++) {
                cards.add(leaders.get(i + j));
            }

            List<List<String>> rows = new ArrayList<>();
            for (int j = 0; j < 4 && j < leaders.size() - i; j++)
                rows.add(Arrays.asList(new LeaderCard(cards.get(j)).getString(cli).split("\\R")));

            int length = rows.stream().map(List::size).reduce(Integer::max).orElse(0);
            for (int k = 0; k < length; k++) {
                stringBuilder.append("│");
                for (int j = 0; j < 4 && j < leaders.size() - i; j++) {
                    if (k < rows.get(j).size())
                        stringBuilder.append(Cli.left(rows.get(j).get(k), cellWidth - 2)).append(" │");
                    else
                        stringBuilder.append(Cli.left("", cellWidth - 2)).append(" │");
                }
                stringBuilder.append("\n");
            }
        }
        stringBuilder.append(Cli.slimLine(cellWidth * (leaders.size() % 5) + 1));

        return Cli.center(stringBuilder.toString());
    }
}
