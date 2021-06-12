package it.polimi.ingsw.client.cli.components;

import it.polimi.ingsw.client.cli.Cli;
import it.polimi.ingsw.client.cli.Renderable;

public abstract class StringComponent implements Renderable {
    public abstract String getString(Cli cli);

    @Override
    public final void render(Cli cli) {
        cli.getOut().println(getString(cli));
    }
}
