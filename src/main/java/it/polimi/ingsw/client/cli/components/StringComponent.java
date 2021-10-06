package it.polimi.ingsw.client.cli.components;

import it.polimi.ingsw.client.cli.Cli;
import it.polimi.ingsw.client.cli.Renderable;

import static it.polimi.ingsw.client.cli.Cli.center;

/**
 * CLI abstract component that provides a common method in order to render components.
 */
public abstract class StringComponent implements Renderable {
    public abstract String getString();

    @Override
    public final void render() {
        Cli cli = Cli.getInstance();

        cli.getOut().println(center(getString()));
        cli.getOut().flush();
    }
}
