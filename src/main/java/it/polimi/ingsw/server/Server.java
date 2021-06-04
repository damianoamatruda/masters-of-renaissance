package it.polimi.ingsw.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.polimi.ingsw.common.Network;
import it.polimi.ingsw.common.NetworkHandler;
import it.polimi.ingsw.common.NetworkProtocol;
import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.backend.Controller;
import it.polimi.ingsw.common.backend.model.FileGameFactory;
import it.polimi.ingsw.common.backend.model.GameFactory;
import it.polimi.ingsw.common.backend.model.Lobby;
import it.polimi.ingsw.common.events.netevents.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

public class Server implements Network {
    private static final String serverConfigPath = "/config/server.json"; // TODO: Share this constant with client
    private static final String defaultGameConfigPath = "/config/config.json"; // TODO: Share this constant with OfflineClient

    private final int port;
    private final InputStream gameConfigStream;
    private final ExecutorService executor;
    private final NetworkProtocol protocol;
    private ServerSocket serverSocket;
    // private final Map<Socket, View> views;
    private volatile boolean listening;

    public Server(int port, InputStream gameConfigStream) {
        this.port = port;
        this.gameConfigStream = gameConfigStream;
        this.executor = Executors.newCachedThreadPool();
        this.protocol = new NetworkProtocol();
        this.serverSocket = null;
        this.listening = false;
    }

    public static void main(String[] args) {
        int port = -1;
        String gameConfigPath = null;
        InputStream gameConfigStream = null;

        Supplier<JsonObject> serverConfigSupplier = new Supplier<>() {
            private JsonObject object;

            @Override
            public JsonObject get() {
                if (object == null)
                    object = new Gson().fromJson(new InputStreamReader(Objects.requireNonNull(Server.class.getResourceAsStream(serverConfigPath))), JsonObject.class);
                return object;
            }
        };

        List<String> arguments = Arrays.asList(args);

        if (arguments.contains("--port")) {
            if (arguments.indexOf("--port") + 1 < arguments.size())
                port = Integer.parseUnsignedInt(arguments.get(arguments.indexOf("--port") + 1));
        }

        if (port == -1)
            port = serverConfigSupplier.get().get("port").getAsInt();

        if (arguments.contains("--config")) {
            if (arguments.indexOf("--config") + 1 < arguments.size())
                gameConfigPath = arguments.get(arguments.indexOf("--config") + 1);
        }

        if (gameConfigPath != null) {
            try {
                gameConfigStream = new FileInputStream(gameConfigPath);
            } catch (FileNotFoundException e) {
                System.err.printf("Couldn't access to file %s.%n", gameConfigPath);
                return;
            }
        }

        try {
            new Server(port, gameConfigStream).start();
        } catch (IOException e) {
            System.err.printf("Couldn't listen on port %d.%n", port);
        }
    }

    public void start() throws IOException {
        serverSocket = new ServerSocket(port);

        GameFactory gameFactory = new FileGameFactory(getClass().getResourceAsStream(defaultGameConfigPath));
        Lobby model = new Lobby(gameFactory);
        Controller controller = new Controller(model);

        System.out.println("Server ready");
        listening = true;
        while (listening) {
            Socket socket;

            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                // System.err.println("Couldn't listen for a connection.");
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
        executor.shutdownNow();
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
