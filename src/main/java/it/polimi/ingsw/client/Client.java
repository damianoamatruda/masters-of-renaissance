package it.polimi.ingsw.client;

import it.polimi.ingsw.client.cli.Cli;
import it.polimi.ingsw.client.gui.Gui;

import java.util.Arrays;

public class Client {
    private final boolean useGui;

    public Client(boolean useGui) {
        this.useGui = useGui;
    }

    public static void main(String[] args) {
        boolean useGui = !Arrays.asList(args).contains("--cli");
        new Client(useGui).start();
    }

    public void start() {
        System.out.println(useGui ? "Launching GUI..." : "Launching CLI...");
        Ui ui = useGui ? new Gui() : new Cli();
        ui.execute(); // new Thread(ui::execute).start();
    }
}
