package it.polimi.ingsw.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.polimi.ingsw.common.EventListener;
import it.polimi.ingsw.common.*;
import it.polimi.ingsw.common.backend.Controller;
import it.polimi.ingsw.common.backend.model.FileGameFactory;
import it.polimi.ingsw.common.backend.model.Lobby;
import it.polimi.ingsw.common.events.vcevents.ReqQuit;
import it.polimi.ingsw.common.events.vcevents.VCEvent;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Server application for the Masters of Renaissance game. */
public class Server implements Network, Runnable {
    private static final Logger LOGGER = Logger.getLogger(Server.class.getName());

    /** Timeout used for heartbeat events. */
    private static final int timeout = 25000;

    /** Default server configuration path. */
    private static final String serverConfigPath = "/config/server.json";

    /** Default game data file path. */
    private static final String defaultGameConfigPath = "/config/config.json";

    /** Thread to run the server on. */
    private final Thread runThread;

    /** Socket to accept incoming connections to. */
    private final ServerSocket serverSocket;

    /** Executor to run client hook threads on. */
    private final ExecutorService executor;

    /** JSON message (de)serializer. */
    private final NetworkProtocol protocol;

    /** Server's Lobby to welcome connecting players and handle new games/reconnections. */
    private final Lobby model;

    /** Server's Controller to handle requests from clients. */
    private final Controller controller;

    /** Map associating NetworkHandlers with VirtualViews' dispatch methods. */
    private final Map<NetworkHandler, EventListener<VCEvent>> vcEventListeners;

    /**
     * Class constructor.
     *
     * @param port             the network port to listen for incoming connections on.
     * @param gameConfigStream the data stream to be used by the GameFactory to create new games.
     * @throws IOException if an I/O error occurs when opening the socket.
     */
    public Server(int port, InputStream gameConfigStream) throws IOException {
        this.runThread = new Thread(this);
        this.serverSocket = new ServerSocket(port);
        this.executor = Executors.newCachedThreadPool();
        this.protocol = new NetworkProtocol();
        this.model = new Lobby(new FileGameFactory(gameConfigStream != null ? gameConfigStream : getClass().getResourceAsStream(defaultGameConfigPath)));
        this.controller = new Controller(model);
        this.vcEventListeners = new HashMap<>();
    }

    /**
     * Class constructor for a Server that uses the default game data file.
     *
     * @param port the network port to listen for incoming connections on.
     * @throws IOException if an I/O error occurs when opening the socket.
     */
    public Server(int port) throws IOException {
        this(port, null);
    }

    public static void main(String[] args) {
        LoggerManager.useLogLevelEnv(Level.ALL);

        Supplier<JsonObject> serverConfigSupplier = new Supplier<>() {
            private JsonObject object;

            @Override
            public JsonObject get() {
                if (object == null)
                    object = new Gson().fromJson(new InputStreamReader(Objects.requireNonNull(
                            getClass().getResourceAsStream(serverConfigPath))), JsonObject.class);
                return object;
            }
        };

        List<String> arguments = Arrays.asList(args);

        int portIndex = arguments.indexOf("--port");
        int port;
        if (portIndex >= 0 && portIndex + 1 < arguments.size()) {
            String portStr = arguments.get(portIndex + 1);
            try {
                port = Integer.parseUnsignedInt(portStr);
            } catch (NumberFormatException e) {
                LOGGER.log(Level.SEVERE, String.format("'%s' is not a valid port", portStr), e);
                return;
            }
        } else
            port = serverConfigSupplier.get().get("port").getAsInt();

        int configIndex = arguments.indexOf("--config");
        InputStream gameConfigStream = null;
        if (configIndex >= 0 && configIndex + 1 < arguments.size()) {
            String gameConfigPath = arguments.get(configIndex + 1);
            try {
                gameConfigStream = new FileInputStream(gameConfigPath);
                LOGGER.info("Loaded custom config");
            } catch (FileNotFoundException e) {
                LOGGER.log(Level.SEVERE, String.format("Could not gain access to file %s", gameConfigPath), e);
                return;
            }
        }

        try {
            new Server(port, gameConfigStream).open();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, String.format("Could not listen on port %d", port), e);
        }
    }

    @Override
    public void open() {
        runThread.start();
    }

    @Override
    public void close() {
        runThread.interrupt();
        if (!serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException ignored) {
            }
        }
    }

    @Override
    public void run() {
        LOGGER.info(String.format("Server is listening on port %d", serverSocket.getLocalPort()));

        /* Routine to accept new clients */
        while (!Thread.currentThread().isInterrupted()) {
            Socket socket;

            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                break;
            }

            NetworkHandler networkHandler = new NetworkHandler(socket, protocol, (input, protocol) -> protocol.processInputAsVCEvent(input), timeout);

            View virtualView = new VirtualView(networkHandler);

            /* Hook the VirtualView to the NetworkHandler, so it can receive the client's events from the network */
            networkHandler.addEventListener(VCEvent.class, vcEventListeners.computeIfAbsent(networkHandler, n -> virtualView::dispatch));

            /* Hook the VirtualView to the Lobby, so it can receive events from it */
            virtualView.registerOnModelLobby(model);

            /* Hook the Controller to the VirtualView, so events can be processed by the Controller */
            controller.registerOnVC(virtualView);

            networkHandler.setOnClose(() -> {
                /* ReqQuit needs to be dispatched directly from virtual view in order to be completely synchronous,
                 * as it must be sent before closing the asynchronous event dispatcher NetworkHandler */
                virtualView.awaitDispatch(new ReqQuit());

                virtualView.unregisterOnModelLobby(model);
                controller.unregisterOnVC(virtualView);
                networkHandler.removeEventListener(VCEvent.class, vcEventListeners.remove(networkHandler));
            });

            executor.submit(networkHandler);
        }

        LOGGER.info("Server closed by self");
        vcEventListeners.keySet().forEach(NetworkHandler::close);
        executor.shutdown();
        model.close();
    }
}
