package it.polimi.ingsw.client.cli;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.polimi.ingsw.client.NetworkClient;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.Objects;

public class MultiplayerState extends CliState {
    private static final String jsonConfigPath = "/config/server.json";

    @Override
    public void render(Cli cli) {
        cli.clear();
        renderMainTitle(cli);
        for (int i = 0; i < 2; i++)
            cli.getOut().println();

        JsonObject jsonConfig = new Gson().fromJson(new InputStreamReader(Objects.requireNonNull(NetworkClient.class.getResourceAsStream(jsonConfigPath))), JsonObject.class);

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

        boolean connected = true;
        try {
            cli.startNetworkClient(host, port);
        } catch (UnknownHostException e) {
            connected = false;
            cli.getOut().printf("Don't know about host %s%n", host);
        } catch (IOException e) {
            connected = false;
            cli.getOut().printf("Couldn't get I/O for the connection to %s when creating the socket%n", host);
        }

        if (connected)
            cli.setState(new InputNicknameState());
        else {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ignored) {
            }
            cli.setState(new MultiplayerState());
        }
    }
}
