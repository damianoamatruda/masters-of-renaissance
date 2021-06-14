package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.cli.components.Menu;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.LinkedHashMap;
import java.util.Map;

import static it.polimi.ingsw.client.cli.Cli.center;

public class OptionsState extends CliController {
    @Override
    public void render(Cli cli) {
        cli.getOut().println();
        cli.getOut().println(center("~ Offline Play Config ~"));

        cli.getOut().println();
        Map<Character, Menu.Entry> entries = new LinkedHashMap<>();
        entries.put('D', new Menu.Entry("Default Config", this::defaultConfig));
        entries.put('C', new Menu.Entry("Custom Config", this::customConfig));
        new Menu(entries, this::goBack).render(cli);
    }

    private void goBack(Cli cli) {
        cli.setState(new MainMenuState());
    }

    private void defaultConfig(Cli cli) {
        cli.getUi().setGameConfigStream(null);
        cli.setState(new MainMenuState());
    }

    private void customConfig(Cli cli) {
        cli.promptFile("Path of custom config.json").ifPresentOrElse(gameConfigFile -> {
            try {
                cli.getUi().setGameConfigStream(new FileInputStream(gameConfigFile));
                cli.setState(new MainMenuState());
            } catch (FileNotFoundException e) {
                cli.getOut().println();
                cli.getOut().printf("Couldn't gain access to file %s.%n", gameConfigFile.getPath());
                cli.promptPause();
                cli.setState(this);
            }
        }, () -> cli.setState(this));
    }
}
