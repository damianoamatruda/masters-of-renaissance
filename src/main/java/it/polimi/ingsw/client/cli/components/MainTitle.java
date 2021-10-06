package it.polimi.ingsw.client.cli.components;

import it.polimi.ingsw.client.cli.Cli;
import it.polimi.ingsw.client.cli.Renderable;

import static it.polimi.ingsw.client.cli.Cli.center;
import static it.polimi.ingsw.client.cli.Cli.convertStreamToString;

/**
 * CLI component that renders the main title.
 */
public class MainTitle implements Renderable {
    @Override
    public void render() {
        Cli cli = Cli.getInstance();

        cli.getOut().println();
        cli.getOut().println(center(convertStreamToString(getClass().getResourceAsStream("/assets/cli/title.txt"))));
        for (int i = 0; i < 2; i++)
            cli.getOut().println();
    }
}
