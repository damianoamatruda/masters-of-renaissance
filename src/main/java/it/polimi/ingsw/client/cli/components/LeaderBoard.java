package it.polimi.ingsw.client.cli.components;

import it.polimi.ingsw.client.ViewModel;
import it.polimi.ingsw.client.cli.Cli;

import static it.polimi.ingsw.client.cli.Cli.center;

public class LeaderBoard extends StringComponent {
    @Override
    public String getString() {
        ViewModel vm = Cli.getInstance().getViewModel();

        StringBuilder stringBuilder = new StringBuilder(center("Leaderboard:"));
        stringBuilder.append(center("Nickname\tFaith Points\tVictory points"));

        vm.getPlayers().forEach(p ->
                stringBuilder.append(center(String.format("%s\t%d\t%d", p.getNickname(), p.getFaithPoints(), p.getVictoryPoints()))));

        return stringBuilder.toString();
    }
}
