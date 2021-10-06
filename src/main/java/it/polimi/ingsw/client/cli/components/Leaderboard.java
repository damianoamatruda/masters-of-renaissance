package it.polimi.ingsw.client.cli.components;

import it.polimi.ingsw.client.ViewModel;
import it.polimi.ingsw.client.cli.Cli;

public class Leaderboard extends StringComponent {
    @Override
    public String getString() {
        ViewModel vm = Cli.getInstance().getViewModel();

        StringBuilder stringBuilder = new StringBuilder("Leaderboard:").append("\n").append("\n");
        stringBuilder.append("Nickname - Faith Points - Victory points").append("\n");

        vm.getPlayers().forEach(p ->
                stringBuilder.append(String.format("%s - %d - %d%n", p.getNickname(), p.getFaithPoints(), p.getVictoryPoints())));

        return stringBuilder.toString();
    }
}
