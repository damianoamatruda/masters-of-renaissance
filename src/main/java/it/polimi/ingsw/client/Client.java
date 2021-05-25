package it.polimi.ingsw.client;

import it.polimi.ingsw.client.cli.Cli;
import it.polimi.ingsw.client.gui.Gui;

import java.util.Arrays;

public class Client {
    public static void main(String[] args) {
        boolean useGui = !Arrays.asList(args).contains("--cli");
        if (useGui)
            Gui.main(args);
        else
            Cli.main(args);
    }
}
