package it.polimi.ingsw.client.cli;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.polimi.ingsw.client.ClientServerHandler;
import it.polimi.ingsw.client.Ui;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

public class Cli implements Ui {
    private final static String jsonConfigPath = "/config/server.json";

    @Override
    public void execute() {
        JsonObject jsonConfig = new Gson().fromJson(new InputStreamReader(Objects.requireNonNull(ClientServerHandler.class.getResourceAsStream(jsonConfigPath))), JsonObject.class);

        String host = jsonConfig.get("host").getAsString();
        int port = jsonConfig.get("port").getAsInt();

        try {
            new ClientServerHandler(host, port).start();
        } catch (IOException e) {
            System.exit(1);
        }
    }
}
