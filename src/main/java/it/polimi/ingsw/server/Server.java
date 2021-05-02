package it.polimi.ingsw.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.polimi.ingsw.server.controller.Controller;
import it.polimi.ingsw.server.model.FileGameFactory;
import it.polimi.ingsw.server.model.GameFactory;
import it.polimi.ingsw.server.model.Lobby;
import it.polimi.ingsw.server.view.View;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements NicknameRegister {
    private final static String jsonConfigPath = "/server.json";

    private final int port;
    // private final Map<Socket, View> views;
    private final Map<View, String> nicknames;
    private volatile boolean listening;

    public Server(int port) {
        this.port = port;
        this.nicknames = new HashMap<>();
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

    public void registerNickname(View view, String nickname) {
        if (nicknames.containsKey(view))
            throw new RuntimeException("Client already has nickname \"" + nicknames.get(view) + "\".");
        if (nickname == null || nickname.isBlank())
            throw new RuntimeException("Given nickname is empty.");
        if (nicknames.containsValue(nickname))
            throw new RuntimeException("Nickname \"" + nickname + "\" already in use. Choose another one.");
        nicknames.put(view, nickname);
    }

    public Optional<String> getNickname(View view) {
        return Optional.ofNullable(nicknames.get(view));
    }

    public void execute() {
        ExecutorService executor = Executors.newCachedThreadPool();

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            GameFactory gameFactory = new FileGameFactory(getClass().getResourceAsStream("/config.json"));
            Lobby model = new Lobby(gameFactory);
            Controller controller = new Controller(this, model);
            GameProtocol gp = new GameProtocol();

            System.out.println("Server ready");
            listening = true;
            while (listening) {
                Socket clientSocket = serverSocket.accept();
                View view = new View(controller);
                // String nickname = getNickname(view).orElse(null);
                executor.submit(new ServerClientHandler(this, clientSocket, view, gp));
            }
            executor.shutdown();
        } catch (IOException e) {
            System.err.println("Exception caught when trying to listen on port " + port);
            System.err.println(e.getMessage());
        }
    }
}
