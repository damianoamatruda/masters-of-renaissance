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
        cli.setController(new MainMenuController());
    }

    private void defaultConfig() {
        cli.getUi().setGameConfigStream(null);
        cli.setController(new MainMenuController());
    }

    private void customConfig() {
        cli.promptFile("Path of custom config.json").ifPresentOrElse(gameConfigFile -> {
            try {
                cli.getUi().setGameConfigStream(new FileInputStream(gameConfigFile));
                cli.setController(new MainMenuController());
                cli.alert("Config loaded.");
            } catch (FileNotFoundException e) {
                cli.alert(String.format("Could not gain access to file %s.", gameConfigFile.getPath()));
                cli.setController(this);
            }
        }, () -> cli.setController(this));
    }
}
