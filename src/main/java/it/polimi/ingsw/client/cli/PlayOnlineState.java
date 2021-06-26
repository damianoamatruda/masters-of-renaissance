package it.polimi.ingsw.client.cli;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.polimi.ingsw.client.OnlineClient;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import static it.polimi.ingsw.client.cli.Cli.center;

public class PlayOnlineState extends CliController {
    private static final String serverConfigPath = "/config/server.json";
    
    @Override
    public void render() {
        cli.getOut().println();
        cli.getOut().println(center("~ Play Online ~"));

        cli.getOut().println();

        JsonObject jsonConfig = new Gson().fromJson(new InputStreamReader(Objects.requireNonNull(OnlineClient.class.getResourceAsStream(serverConfigPath))), JsonObject.class);

        String defaultHost = jsonConfig.get("host").getAsString();
        int defaultPort = jsonConfig.get("port").getAsInt();

        AtomicBoolean valid = new AtomicBoolean(false);
        while (!valid.get()) {
            valid.set(true);
            cli.prompt("Server address", String.format("%s:%s", defaultHost, defaultPort)).ifPresentOrElse(address -> {
                if (address.contains(":")) {
                    String[] addressTokens = address.split(":", 2);

                    if (addressTokens.length == 2) {
                        String inputHost = addressTokens[0];
                        int inputPort;

                        try {
                            inputPort = Integer.parseInt(addressTokens[1]);
                            connect(inputHost, inputPort);
                        } catch (NumberFormatException ignored) {
                            valid.set(false);
                        }
                    }
                } else
                    valid.set(false);
            }, () -> cli.setController(new MainMenuState(), false));
        }
    }

    private void connect(String host, int port) {
        boolean connected = false;
        try {
            cli.getUi().openOnlineClient(host, port);
            connected = true;
        } catch (UnknownHostException e) {
            cli.getOut().println();
            cli.getOut().printf("Don't know about host %s%n", host);
        } catch (IOException e) {
            cli.getOut().println();
            cli.getOut().println("Could not connect to the server.");
        }

        if (connected)
            cli.setController(new InputNicknameState("Play Online"), false);
        else {
            cli.setController(new PlayOnlineState(), true);
        }
    }
}
