package it.polimi.ingsw.client.cli.components;

import it.polimi.ingsw.client.cli.Cli;
import it.polimi.ingsw.common.reducedmodel.ReducedLeaderCard;

import java.util.ArrayList;
import java.util.List;

import static it.polimi.ingsw.client.cli.Cli.center;
import static it.polimi.ingsw.client.cli.Cli.maxLinesHeight;

public class LeadersHand extends StringComponent {
    private final List<ReducedLeaderCard> leaders;

    public LeadersHand(List<ReducedLeaderCard> leaders) {
        this.leaders = leaders;
    }

    @Override
    public String getString(Cli cli) {
        StringBuilder stringBuilder = new StringBuilder();

        int i;
        for (i = 0; i < leaders.size(); i += 4) {
            List<ReducedLeaderCard> cards = new ArrayList<>();
            for (int j = 0; j < 4 && j < leaders.size() - i; j++) {
                cards.add(leaders.get(i + j));
            }

            List<LeaderCard> leaderCardComponents = new ArrayList<>();
            for (int j = 0; j < 4 && j < leaders.size() - i; j++)
                leaderCardComponents.add(new LeaderCard(cards.get(j)));

            int maxWidth = leaderCardComponents.stream().map(c -> c.getString(cli)).mapToInt(Cli::maxLineWidth).max().orElse(0);
            int maxHeight = maxLinesHeight(leaderCardComponents.stream().map(c -> c.getString(cli)).toList());

            List<List<String>> rows = new ArrayList<>();
            for (LeaderCard leaderCardComponent : leaderCardComponents)
                rows.add(new Box(leaderCardComponent, -1, maxWidth, maxHeight).getString(cli).lines().toList());

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
