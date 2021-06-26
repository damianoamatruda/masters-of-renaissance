package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.cli.components.MainTitle;
import it.polimi.ingsw.client.cli.components.Menu;

import java.util.LinkedHashMap;
import java.util.Map;

public class MainMenuState extends CliController {
    @Override
    public void render() {
        new MainTitle().render();

        Map<Character, Menu.Entry> entries = new LinkedHashMap<>();
        entries.put('1', new Menu.Entry("Play Offline", this::playOffline));
        entries.put('2', new Menu.Entry("Play Online", cli1 -> cli1.setController(new PlayOnlineState(), false)));
        entries.put('O', new Menu.Entry("Options...", cli1 -> cli1.setController(new OptionsState(), false)));
        entries.put('Q', new Menu.Entry("Quit Game", Cli::quit));
        new Menu(entries, Cli::quit).render();
    }

    private void playOffline(Cli cli) {
        cli.getUi().openOfflineClient();
        cli.setController(new InputNicknameState("Play Offline"), false);
    }
}
