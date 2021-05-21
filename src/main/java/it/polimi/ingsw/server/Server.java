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
            JsonObject jsonConfig = new Gson().fromJson(new InputStreamReader(Objects.requireNonNull(Server.class.getResourceAsStream(serverConfigPath))), JsonObject.class);
            port = jsonConfig.get("port").getAsInt();
        }

        new Server(port).execute();
    }

    public void execute() {
        ExecutorService executor = Executors.newCachedThreadPool();
        listening = true;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            GameFactory gameFactory = new FileGameFactory(getClass().getResourceAsStream(gameConfigPath));
            Lobby model = new Lobby(gameFactory);
            Controller controller = new Controller(model);

            NetworkProtocol protocol = new NetworkProtocol();

            System.out.println("Server ready");
            while (listening) {
                Socket clientSocket = serverSocket.accept();
                NetworkHandler networkHandler = new ServerClientHandler(clientSocket, protocol);

                networkHandler.addEventListener(ReqWelcome.class, event -> on(event, networkHandler));
                networkHandler.addEventListener(ResWelcome.class, event -> on(event, networkHandler));
                networkHandler.addEventListener(ReqHeartbeat.class, event -> on(event, networkHandler));
                networkHandler.addEventListener(ResHeartbeat.class, event -> on(event, networkHandler));
                networkHandler.addEventListener(ReqGoodbye.class, event -> on(event, networkHandler));
                networkHandler.addEventListener(ResGoodbye.class, event -> on(event, networkHandler));

                View view = new VirtualView();
                view.registerOnVC(networkHandler);
                controller.registerOnVC(view);

                networkHandler.registerOnMV(view);

                executor.submit(networkHandler);

                // TODO: Unregister when the socket or the server is closed
            }
        } catch (IOException e) {
            System.err.println("Exception caught when trying to listen on port " + port);
            System.err.println(e.getMessage());
        } finally {
            executor.shutdown();
            listening = false;
        }
    }

    public void stop() {
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
