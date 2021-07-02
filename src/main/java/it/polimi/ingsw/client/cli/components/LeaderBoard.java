package it.polimi.ingsw.client.cli.components;

import it.polimi.ingsw.client.ViewModel;
import it.polimi.ingsw.client.cli.Cli;

import static it.polimi.ingsw.client.cli.Cli.center;

public class LeaderBoard extends StringComponent {
    @Override
    public String getString() {
        Cli cli = Cli.getInstance();
        ViewModel vm = cli.getViewModel();

        StringBuilder sb = new StringBuilder(center("Leaderboard:"));
        sb.append(center("Nickname\tFaith Points\tVictory points"));

        vm.getPlayers().forEach(p ->
            sb.append(center(String.format("%s\t%d\t%d", p.getNickname(), p.getFaithPoints(), p.getVictoryPoints()))));

        return sb.toString();
    }
}
