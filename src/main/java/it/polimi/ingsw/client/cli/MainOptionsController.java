package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.cli.components.Menu;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.LinkedHashMap;
import java.util.Map;

import static it.polimi.ingsw.client.cli.Cli.center;

public class MainOptionsController extends CliController {
    @Override
    public void render() {
        cli.getOut().println();
        cli.getOut().println(center("~ Offline Play Config ~"));

        cli.getOut().println();
        Map<Character, Menu.Entry> entries = new LinkedHashMap<>();
        entries.put('D', new Menu.Entry("Default Config", this::defaultConfig));
        entries.put('C', new Menu.Entry("Custom Config", this::customConfig));
        new Menu(entries, this::goBack).render();
    }

    private void goBack() {
        cli.setController(new MainMenuController(), false);
    }

    private void defaultConfig() {
        cli.getUi().setGameConfigStream(null);
        cli.setController(new MainMenuController(), false);
    }

    private void customConfig() {
        cli.promptFile("Path of custom config.json").ifPresentOrElse(gameConfigFile -> {
            try {
                cli.getUi().setGameConfigStream(new FileInputStream(gameConfigFile));
                cli.setController(new MainMenuController(), false);
                cli.getOut().println(center("Config loaded."));
                cli.promptPause();
            } catch (FileNotFoundException e) {
                cli.getOut().println();
                cli.getOut().printf("Couldn't gain access to file %s.%n", gameConfigFile.getPath());
                cli.setController(this, true);
            }
        }, () -> cli.setController(this, false));
    }
}
