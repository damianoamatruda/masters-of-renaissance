package it.polimi.ingsw.client.cli;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.polimi.ingsw.client.OnlineClient;
import it.polimi.ingsw.client.cli.components.MainTitle;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class PlayOnlineState extends CliState {
    private static final String serverConfigPath = "/config/server.json";

    @Override
    public void render(Cli cli) {
        new MainTitle().render(cli);

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
                            connect(cli, inputHost, inputPort);
                        } catch (NumberFormatException ignored) {
                            valid.set(false);
                        }
                    }
                } else
                    valid.set(false);
            }, () -> cli.setState(new MainMenuState()));
        }
    }

    private void connect(Cli cli, String host, int port) {
        boolean connected = false;
        try {
            cli.openOnlineClient(host, port);
            connected = true;
        } catch (UnknownHostException e) {
            cli.getOut().println();
            cli.getOut().printf("Don't know about host %s%n", host);
        } catch (IOException e) {
            cli.getOut().println();
            cli.getOut().printf("Couldn't get I/O for the connection to %s when creating the socket.%n", host);
        }

        if (connected)
            cli.setState(new InputNicknameState());
        else {
            cli.promptPause();
            cli.setState(new PlayOnlineState());
        }
    }
}
