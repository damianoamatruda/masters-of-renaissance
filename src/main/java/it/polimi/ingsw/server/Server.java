package it.polimi.ingsw.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.polimi.ingsw.common.NetworkHandler;
import it.polimi.ingsw.common.NetworkProtocol;
import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.backend.Controller;
import it.polimi.ingsw.common.backend.model.FileGameFactory;
import it.polimi.ingsw.common.backend.model.GameFactory;
import it.polimi.ingsw.common.backend.model.Lobby;
import it.polimi.ingsw.common.events.netevents.*;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final static String serverConfigPath = "/config/server.json"; // TODO: Share this constant with client
    private final static String gameConfigPath = "/config/config.json"; // TODO: Share this constant with LocalClient

    private final int port;
    private final ExecutorService executor;
    private final NetworkProtocol protocol;
    private ServerSocket serverSocket;
    // private final Map<Socket, View> views;
    private volatile boolean listening;

    public Server(int port) {
        this.port = port;
        this.executor = Executors.newCachedThreadPool();
        this.protocol = new NetworkProtocol();
        this.serverSocket = null;
        this.listening = false;
    }

    public static void main(String[] args) {
        int port;

        if (args.length >= 1)
            port = Integer.parseInt(args[0]);
        else {
            JsonObject jsonConfig = new Gson().fromJson(new InputStreamReader(Objects.requireNonNull(Server.class.getResourceAsStream(serverConfigPath))), JsonObject.class);
            port = jsonConfig.get("port").getAsInt();
        }

        boolean connected = true;
        try {
            new Server(port).start();
        } catch (IOException e) {
            connected = false;
            System.err.printf("Exception caught when trying to listen on port %d%n", port);
        }
    }

    public void start() throws IOException {
        serverSocket = new ServerSocket(port);

        GameFactory gameFactory = new FileGameFactory(getClass().getResourceAsStream(gameConfigPath));
        Lobby model = new Lobby(gameFactory);
        Controller controller = new Controller(model);

        System.out.println("Server ready");
        listening = true;
        while (listening) {
            Socket socket;

            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                // System.err.println("Exception caught when listening for a connection");
                // System.err.println(e.getMessage());
                continue;
            }

            NetworkHandler networkHandler = new ServerClientHandler(socket, protocol);

            networkHandler.addEventListener(ReqWelcome.class, event -> on(event, networkHandler));
            networkHandler.addEventListener(ResWelcome.class, event -> on(event, networkHandler));
            networkHandler.addEventListener(ReqHeartbeat.class, event -> on(event, networkHandler));
            networkHandler.addEventListener(ResHeartbeat.class, event -> on(event, networkHandler));
            networkHandler.addEventListener(ReqGoodbye.class, event -> on(event, networkHandler));
            networkHandler.addEventListener(ResGoodbye.class, event -> on(event, networkHandler));

            View virtualView = new View();
            virtualView.registerOnModelLobby(model); // TODO: Unregister when done
            virtualView.registerOnVC(networkHandler);
            controller.registerOnVC(virtualView);

            networkHandler.registerOnMV(virtualView);

            executor.submit(networkHandler);
        }
        stop();
    }

    public void stop() {
        executor.shutdown();
        listening = false;
    }

    private void on(ReqWelcome event, NetworkHandler networkHandler) {
        networkHandler.send(new ResWelcome());
    }

    private void on(ResWelcome event, NetworkHandler networkHandler) {
    }

    private void on(ReqHeartbeat event, NetworkHandler networkHandler) {
        networkHandler.send(new ResHeartbeat());
    }

    private void on(ResHeartbeat event, NetworkHandler networkHandler) {
    }

    private void on(ReqGoodbye event, NetworkHandler networkHandler) {
        networkHandler.send(new ResGoodbye());
        networkHandler.stop();
    }

    private void on(ResGoodbye event, NetworkHandler networkHandler) {
        networkHandler.stop();
    }
}
