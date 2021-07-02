package it.polimi.ingsw.client.cli.components;

import it.polimi.ingsw.client.ViewModel;
import it.polimi.ingsw.client.cli.Cli;

public class LeaderBoard extends StringComponent {
    @Override
    public String getString() {
        ViewModel vm = Cli.getInstance().getViewModel();

        StringBuilder stringBuilder = new StringBuilder("Leaderboard:\n");
        stringBuilder.append("Nickname - Faith Points - Victory points\n");

        vm.getPlayers().forEach(p -> {
            stringBuilder.append(String.format("%s - %d - %d%n", p.getNickname(), p.getFaithPoints(), p.getVictoryPoints()));
        });

        return stringBuilder.toString();
    }
}
