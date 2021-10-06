package it.polimi.ingsw.client;

import it.polimi.ingsw.client.cli.Cli;
import it.polimi.ingsw.client.gui.Gui;
import it.polimi.ingsw.common.LoggerManager;

import java.util.Arrays;
import java.util.logging.Level;

/**
 * This class represents the client application of the Masters of Renaissance game.
 */
public class Client {
    public static void main(String[] args) {
        LoggerManager.useLogLevelEnv(Level.OFF);

        boolean useGui = !Arrays.asList(args).contains("--cli");
        if (useGui)
            Gui.main(args);
        else
            Cli.main(args);
    }
}
