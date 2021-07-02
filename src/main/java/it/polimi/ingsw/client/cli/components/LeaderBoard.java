package it.polimi.ingsw.client.cli.components;

import it.polimi.ingsw.client.ViewModel;
import it.polimi.ingsw.client.cli.Cli;

import static it.polimi.ingsw.client.cli.Cli.left;

public class LeaderBoard extends StringComponent {
    @Override
    public String getString() {
        ViewModel vm = Cli.getInstance().getViewModel();

        StringBuilder stringBuilder = new StringBuilder("Leaderboard:\n");
        stringBuilder.append("Nickname").append(" ".repeat(5)).append("Faith Points").append(" ".repeat(5)).append("Victory points\n");

        vm.getPlayers().forEach(p -> {
            stringBuilder.append(left(String.format("%s", p.getNickname()), 13));
            stringBuilder.append(left(String.format("%d", p.getFaithPoints()), 17));
            stringBuilder.append(left(String.format("%d\n", p.getVictoryPoints()), 19));
        });

        return stringBuilder.toString();
    }
}
