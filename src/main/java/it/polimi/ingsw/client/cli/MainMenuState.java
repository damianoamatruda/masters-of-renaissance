package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.cli.components.Menu;

import java.util.LinkedHashMap;
import java.util.Map;

public class MainMenuState extends CliState {
    @Override
    public void render(Cli cli) {
        cli.clear();
        renderMainTitle(cli);
        for (int i = 0; i < 2; i++)
            cli.getOut().println();
        renderMainMenu(cli);
    }

    private void renderMainMenu(Cli cli) {
        Map<Character, Menu.Entry> entries = new LinkedHashMap<>();
        entries.put('S', new Menu.Entry("Singleplayer", cli1 -> cli.setState(new SingleplayerState())));
        entries.put('M', new Menu.Entry("Multiplayer", cli1 -> cli.setState(new MultiplayerState())));
        entries.put('O', new Menu.Entry("Options...", cli1 -> cli.getOut().println("Launching Options...")));
        entries.put('Q', new Menu.Entry("Quit Game", Cli::quit));
        new Menu(entries).render(cli);
    }
}
