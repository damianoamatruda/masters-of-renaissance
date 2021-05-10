package it.polimi.ingsw.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.polimi.ingsw.common.GameProtocol;
import it.polimi.ingsw.common.backend.Controller;
import it.polimi.ingsw.common.backend.model.FileGameFactory;
import it.polimi.ingsw.common.backend.model.GameFactory;
import it.polimi.ingsw.common.backend.model.Lobby;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final static String jsonConfigPath = "/config/server.json";

    private final int port;
    // private final Map<Socket, View> views;
    private volatile boolean listening;

    public Server(int port) {
        this.port = port;
        this.listening = false;
    }

    public static void main(String[] args) {
        int port;

        if (args.length >= 1)
            port = Integer.parseInt(args[0]);
        else {
            JsonObject jsonConfig = new Gson().fromJson(new InputStreamReader(Objects.requireNonNull(Server.class.getResourceAsStream(jsonConfigPath))), JsonObject.class);
            port = jsonConfig.get("port").getAsInt();
        }

        new Server(port).execute();
    }

    public void execute() {
        ExecutorService executor = Executors.newCachedThreadPool();

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            GameFactory gameFactory = new FileGameFactory(getClass().getResourceAsStream("/config/config.json"));
            Lobby model = new Lobby(gameFactory);
            Controller controller = new Controller(model);
            GameProtocol gp = new GameProtocol();

            System.out.println("Server ready");
            listening = true;
            while (listening) {
                Socket clientSocket = serverSocket.accept();
                VirtualView view = new VirtualView(controller);
                // String nickname = getNickname(view).orElse(null);
                executor.submit(new ServerClientHandler(clientSocket, view, gp));
            }
            executor.shutdown();
        } catch (IOException e) {
            System.err.println("Exception caught when trying to listen on port " + port);
            System.err.println(e.getMessage());
        }
    }
}
