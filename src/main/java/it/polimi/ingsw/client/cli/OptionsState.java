package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.cli.components.MainTitle;
import it.polimi.ingsw.client.cli.components.Menu;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.LinkedHashMap;
import java.util.Map;

public class OptionsState extends CliState {
    @Override
    public void render(Cli cli) {
        new MainTitle().render(cli);

        cli.getOut().print(Cli.center("Offline Play Config:"));

        Map<Character, Menu.Entry> entries = new LinkedHashMap<>();
        entries.put('D', new Menu.Entry("Default Config", this::defaultConfig));
        entries.put('C', new Menu.Entry("Custom Config", this::customConfig));
        new Menu(entries, this::goBack).render(cli);
    }

    private void goBack(Cli cli) {
        cli.setState(new MainMenuState());
    }

    private void defaultConfig(Cli cli) {
        cli.setGameConfigStream(null);
        cli.setState(new MainMenuState());
    }

    private void customConfig(Cli cli) {
        cli.promptFile("Path of custom config.json").ifPresentOrElse(gameConfigFile -> {
            try {
                cli.setGameConfigStream(new FileInputStream(gameConfigFile));
                cli.setState(new MainMenuState());
            } catch (FileNotFoundException e) {
                cli.getOut().printf("Couldn't gain access to file %s.%n", gameConfigFile.getPath());
                cli.promptPause();
                cli.setState(new OptionsState());
            }
        }, () -> cli.setState(new OptionsState()));
    }
}
