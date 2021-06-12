package it.polimi.ingsw.client.cli.components;

import it.polimi.ingsw.client.cli.Cli;
import it.polimi.ingsw.client.cli.CliState;
import it.polimi.ingsw.client.cli.Renderable;

import static it.polimi.ingsw.client.cli.Cli.center;
import static it.polimi.ingsw.client.cli.Cli.convertStreamToString;

public class MainTitle implements Renderable {
    @Override
    public void render(Cli cli) {
        cli.getOut().println();
        cli.getOut().println(center(convertStreamToString(CliState.class.getResourceAsStream("/assets/cli/title.txt"))));
        for (int i = 0; i < 2; i++)
            cli.getOut().println();
    }
}
