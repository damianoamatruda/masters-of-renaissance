package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.cli.components.MainTitle;
import it.polimi.ingsw.client.cli.components.Menu;

import java.util.LinkedHashMap;
import java.util.Map;

public class MainMenuController extends CliController {
    @Override
    public void render() {
        new MainTitle().render();

        Map<Character, Menu.Entry> entries = new LinkedHashMap<>();
        entries.put('1', new Menu.Entry("Play Offline", cli2 -> playOffline()));
        entries.put('2', new Menu.Entry("Play Online", cli1 -> cli1.setController(new PlayOnlineController(), false)));
        entries.put('O', new Menu.Entry("Options...", cli1 -> cli1.setController(new MainOptionsController(), false)));
        entries.put('Q', new Menu.Entry("Quit Game", Cli::quit));
        new Menu(entries, Cli::quit).render();
    }

    private void playOffline() {
        cli.getUi().openOfflineClient();
        cli.setController(new InputNicknameController("Play Offline"), false);
    }
}
