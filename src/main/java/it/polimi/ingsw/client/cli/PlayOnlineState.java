package it.polimi.ingsw.client.cli;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.polimi.ingsw.client.OnlineClient;
import it.polimi.ingsw.client.cli.components.MainTitle;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.Objects;

public class PlayOnlineState extends CliState {
    private static final String serverConfigPath = "/config/server.json";

    @Override
    public void render(Cli cli) {
        new MainTitle().render(cli);

        JsonObject jsonConfig = new Gson().fromJson(new InputStreamReader(Objects.requireNonNull(OnlineClient.class.getResourceAsStream(serverConfigPath))), JsonObject.class);

        String host = jsonConfig.get("host").getAsString();
        int port = jsonConfig.get("port").getAsInt();

        boolean validAddress = false;

        while (!validAddress) {
            String address = cli.prompt("Server address", String.format("%s:%s", host, port));

            if (address.isBlank())
                validAddress = true;
            else if (address.contains(":")) {
                String[] addressTokens = address.split(":", 2);

                if (addressTokens.length == 2) {
                    String inputHost = addressTokens[0];
                    int inputPort;

                    try {
                        inputPort = Integer.parseInt(addressTokens[1]);
                    } catch (NumberFormatException ignored) {
                        continue;
                    }

                    host = inputHost;
                    port = inputPort;
                    validAddress = true;
                }
            }
        }

        cli.getOut().println();

        boolean connected = false;
        try {
            cli.startOnlineClient(host, port);
            connected = true;
        } catch (UnknownHostException e) {
            cli.getOut().printf("Don't know about host %s%n", host);
        } catch (IOException e) {
            cli.getOut().printf("Couldn't get I/O for the connection to %s when creating the socket.%n", host);
        }

        if (connected)
            cli.setState(new InputNicknameState());
        else {
            cli.pause();
            cli.setState(new PlayOnlineState());
        }
    }
}
