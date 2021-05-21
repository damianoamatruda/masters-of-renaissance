package it.polimi.ingsw.client;

import it.polimi.ingsw.client.cli.Cli;
import it.polimi.ingsw.common.NetworkHandler;
import it.polimi.ingsw.common.NetworkProtocol;
import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.events.netevents.*;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NetworkClient {
    private final String host;
    private final int port;
    private final ExecutorService executor;
    private final View view;
    private final Cli cli; // TODO: Make it compatible with other UIs
    private Socket socket;
    private volatile boolean listening;

    public NetworkClient(String host, int port, View view, Cli cli) {
        this.host = host;
        this.port = port;
        this.executor = Executors.newCachedThreadPool();
        this.view = view;
        this.cli = cli;
        this.socket = null;
        this.listening = false;
    }

    public void start() throws IOException {
        listening = true;

        NetworkHandler networkHandler;

        try (Socket socket = new Socket(host, port)) {
            this.socket = socket;

            NetworkProtocol protocol = new NetworkProtocol();
            networkHandler = new ClientServerHandler(socket, protocol);

            networkHandler.addEventListener(ReqWelcome.class, event -> on(event, networkHandler));
            networkHandler.addEventListener(ResWelcome.class, event -> on(event, networkHandler));
            networkHandler.addEventListener(ReqHeartbeat.class, event -> on(event, networkHandler));
            networkHandler.addEventListener(ResHeartbeat.class, event -> on(event, networkHandler));
            networkHandler.addEventListener(ReqGoodbye.class, event -> on(event, networkHandler));
            networkHandler.addEventListener(ResGoodbye.class, event -> on(event, networkHandler));

            view.registerOnVC(cli);
            cli.registerOnMV(view);

            networkHandler.registerOnVC(view);

            view.registerOnModelGame(networkHandler);
            view.registerOnModelPlayer(networkHandler);

            executor.submit(networkHandler);

            while (listening)
                Thread.onSpinWait();

            view.unregisterOnVC(cli);
            cli.unregisterOnMV(view);

            view.unregisterOnModelGame(networkHandler);
            view.unregisterOnModelPlayer(networkHandler);
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + host);
            System.err.println(e.getMessage());
            throw e;
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + host + " when creating the socket");
            System.err.println(e.getMessage());
            throw e;
        } finally {
            executor.shutdown();
            socket = null;
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
        stop();
    }

    private void on(ResGoodbye event, NetworkHandler networkHandler) {
        stop();
    }
}
