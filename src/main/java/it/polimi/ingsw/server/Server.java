package it.polimi.ingsw.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.polimi.ingsw.common.EventListener;
import it.polimi.ingsw.common.*;
import it.polimi.ingsw.common.backend.Controller;
import it.polimi.ingsw.common.backend.model.FileGameFactory;
import it.polimi.ingsw.common.backend.model.GameFactory;
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

public class Server implements Network, Runnable {
    private static final Logger LOGGER = Logger.getLogger(Server.class.getName());
    private static final int timeout = 25000;
    private static final String serverConfigPath = "/config/server.json"; // TODO: Share this constant with client
    private static final String defaultGameConfigPath = "/config/config.json"; // TODO: Share this constant with OfflineClient

    private final ServerSocket serverSocket;
    private final ExecutorService executor;
    private final NetworkProtocol protocol;
    private final Lobby model;
    private final Controller controller;
    private final Map<NetworkHandler, EventListener<VCEvent>> vcEventListeners;
    private volatile boolean listening;

    public Server(int port, InputStream gameConfigStream) throws IOException {
        this.serverSocket = new ServerSocket(port);

        this.executor = Executors.newCachedThreadPool();
        this.protocol = new NetworkProtocol();
        this.listening = false;

        GameFactory gameFactory = new FileGameFactory(gameConfigStream != null ? gameConfigStream : getClass().getResourceAsStream(defaultGameConfigPath));

        this.model = new Lobby(gameFactory);

        this.controller = new Controller(model);

        this.vcEventListeners = new HashMap<>();
    }

    public Server(int port) throws IOException {
        this(port, null);
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
                LOGGER.info("Loaded custom config");
            } catch (FileNotFoundException e) {
                LOGGER.log(Level.SEVERE, String.format("Couldn't gain access to file %s", gameConfigPath), e);
                return;
            }
        }

        try {
            new Server(port, gameConfigStream).run();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, String.format("Couldn't listen on port %d", port), e);
        }
    }

    @Override
    public void open() {
        new Thread(this).start();
    }

    @Override
    public void run() {
        LOGGER.info("Server is ready");
        listening = true;
        while (listening) {
            Socket socket;

            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Couldn't listen for a connection", e);
                continue;
            }

            NetworkHandler networkHandler = new NetworkHandler(socket, protocol, (input, protocol) -> protocol.processInputAsVCEvent(input), timeout);

            View virtualView = new View();
            setVirtualViewListeners(virtualView, networkHandler);
            networkHandler.addEventListener(VCEvent.class, vcEventListeners.computeIfAbsent(networkHandler, n -> virtualView::dispatch));

            virtualView.registerOnModelLobby(model);
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

        close();
    }

    @Override
    public void close() {
        listening = false;
        try {
            serverSocket.close();
        } catch (IOException ignored) {
        }
        executor.shutdownNow();
        model.close();
    }

    private static void setVirtualViewListeners(View virtualView, NetworkHandler networkHandler) {
        virtualView.setResQuitEventListener(networkHandler::send);
        virtualView.setUpdateBookedSeatsEventListener(networkHandler::send);
        virtualView.setUpdateJoinGameEventListener(networkHandler::send);
        virtualView.setErrNewGameEventListener(networkHandler::send);
        virtualView.setErrNicknameEventListener(networkHandler::send);
        virtualView.setErrActionEventListener(networkHandler::send);
        virtualView.setErrActiveLeaderDiscardedEventListener(networkHandler::send);
        virtualView.setErrBuyDevCardEventListener(networkHandler::send);
        virtualView.setErrCardRequirementsEventListener(networkHandler::send);
        virtualView.setErrInitialChoiceEventListener(networkHandler::send);
        virtualView.setErrNoSuchEntityEventListener(networkHandler::send);
        virtualView.setErrObjectNotOwnedEventListener(networkHandler::send);
        virtualView.setErrReplacedTransRecipeEventListener(networkHandler::send);
        virtualView.setErrResourceReplacementEventListener(networkHandler::send);
        virtualView.setErrResourceTransferEventListener(networkHandler::send);
        virtualView.setUpdateActionEventListener(networkHandler::send);
        virtualView.setUpdateActionTokenEventListener(networkHandler::send);
        virtualView.setUpdateCurrentPlayerEventListener(networkHandler::send);
        virtualView.setUpdateDevCardGridEventListener(networkHandler::send);
        virtualView.setUpdateDevCardSlotEventListener(networkHandler::send);
        virtualView.setUpdateFaithPointsEventListener(networkHandler::send);
        virtualView.setUpdateGameEventListener(networkHandler::send);
        virtualView.setUpdateGameEndEventListener(networkHandler::send);
        virtualView.setUpdateLastRoundEventListener(networkHandler::send);
        virtualView.setUpdateActivateLeaderEventListener(networkHandler::send);
        virtualView.setUpdateLeadersHandCountEventListener(networkHandler::send);
        virtualView.setUpdateMarketEventListener(networkHandler::send);
        virtualView.setUpdatePlayerEventListener(networkHandler::send);
        virtualView.setUpdatePlayerStatusEventListener(networkHandler::send);
        virtualView.setUpdateResourceContainerEventListener(networkHandler::send);
        virtualView.setUpdateSetupDoneEventListener(networkHandler::send);
        virtualView.setUpdateVaticanSectionEventListener(networkHandler::send);
        virtualView.setUpdateVictoryPointsEventListener(networkHandler::send);
        virtualView.setUpdateLeadersHandEventListener(networkHandler::send);
    }
}
