package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.cli.components.Menu;

import java.util.LinkedHashMap;
import java.util.Map;

public class MainMenuState extends CliState {
    @Override
    public void render(Cli cli) {
        renderMainTitle(cli);
        for (int i = 0; i < 2; i++)
            cli.getOut().println();
        renderMainMenu(cli);
    }

    private void renderMainMenu(Cli cli) {
        Map<Character, Menu.Entry> entries = new LinkedHashMap<>();
        entries.put('1', new Menu.Entry("Play Offline", cli1 -> cli.setState(new PlayOfflineState())));
        entries.put('2', new Menu.Entry("Play Online", cli1 -> cli.setState(new PlayOnlineState())));
        entries.put('O', new Menu.Entry("Options...", cli1 -> cli.getOut().println("Launching Options...")));
        entries.put('Q', new Menu.Entry("Quit Game", Cli::quit));
        new Menu(entries).render(cli);
    }
}
