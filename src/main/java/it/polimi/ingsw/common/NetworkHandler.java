package it.polimi.ingsw.common;

import it.polimi.ingsw.common.events.Event;
import it.polimi.ingsw.common.events.netevents.*;
import it.polimi.ingsw.common.events.netevents.errors.ErrProtocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.function.BiFunction;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Event dispatcher responsible for transferring messages over the network. */
public class NetworkHandler extends AsynchronousEventDispatcher implements Runnable, AutoCloseable {
    private static final Logger LOGGER = Logger.getLogger(NetworkHandler.class.getName());
    /** Internal dispatcher for NetEvents.
        NetEvents are handled independently from application-level events. */
    private final EventDispatcher netEventDispatcher;
    /** The socket to send and receive messages on. */
    private final Socket socket;
    /** The messages' (de)serializer. */
    private final NetworkProtocol protocol;
    /** Function used to process network-side incoming messages. */
    private final BiFunction<String, NetworkProtocol, Event> processInput;
    /** Heartbeat message timeout. */
    private final int timeout;
    private final EventListener<ReqWelcome> reqWelcomeEventListener = this::on;
    private final EventListener<ReqHeartbeat> reqHeartbeatEventListener = this::on;
    private final EventListener<ResWelcome> resWelcomeEventListener = this::on;
    private final EventListener<ResGoodbye> resGoodbyeEventListener = this::on;
    private final EventListener<ResHeartbeat> resHeartbeatEventListener = this::on;
    private final EventListener<ReqGoodbye> reqGoodbyeEventListener = this::on;
    private PrintWriter out;
    private Runnable onClose = () -> {
    };

    /**
     * Class constructor.
     * 
     * @param socket       the network socket to listen for events on.
     * @param protocol     the message (de)serializer.
     * @param processInput function to use to deserialize messages incoming from the network.
     * @param timeout      timeout (in ms) to use with Heartbeat events.
     */
    public NetworkHandler(Socket socket, NetworkProtocol protocol, BiFunction<String, NetworkProtocol, Event> processInput, int timeout) {
        this.netEventDispatcher = new AsynchronousEventDispatcher();
        this.socket = socket;
        this.protocol = protocol;
        this.processInput = processInput;
        this.timeout = timeout;
    }

    @Override
    public void close() {
        send(new ReqGoodbye());
    }

    public void shutdown() {
        if (socket.isClosed())
            return;
        try {
            socket.close();
        } catch (IOException ignored) {
        }
    }

    @Override
    public void run() {
        netEventDispatcher.addEventListener(ReqWelcome.class, reqWelcomeEventListener);
        netEventDispatcher.addEventListener(ReqHeartbeat.class, reqHeartbeatEventListener);
        netEventDispatcher.addEventListener(ReqGoodbye.class, reqGoodbyeEventListener);
        netEventDispatcher.addEventListener(ResWelcome.class, resWelcomeEventListener);
        netEventDispatcher.addEventListener(ResHeartbeat.class, resHeartbeatEventListener);
        netEventDispatcher.addEventListener(ResGoodbye.class, resGoodbyeEventListener);

        try (
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            this.out = out;
            String inputLine;

            socket.setSoTimeout(timeout / 2);
            boolean halfTimeout = false;

            send(new ReqWelcome());
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    if ((inputLine = in.readLine()) == null) {
                        LOGGER.info("Connection closed ungracefully by peer");
                        break;
                    }

                    LOGGER.info(inputLine);

                    halfTimeout = false;

                    try {
                        netEventDispatcher.dispatch(protocol.processInputAsNetEvent(inputLine));
                    } catch (NetworkProtocolException e1) {
                        try {
                            dispatch(processInput.apply(inputLine, protocol));
                        } catch (NetworkProtocolException e2) {
                            send(new ErrProtocol(e2));
                        }
                    }
                } catch (SocketTimeoutException e) {
                    if (halfTimeout)
                        throw e;
                    send(new ReqHeartbeat());
                    halfTimeout = true;
                }
            }
            LOGGER.info("Connection closed by self");
        } catch (IOException e) {
            LOGGER.info("Connection closed by self after peer");
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Unknown runtime exception", e);
        } finally {
            onClose.run();
            super.close();
            netEventDispatcher.removeEventListener(ReqWelcome.class, reqWelcomeEventListener);
            netEventDispatcher.removeEventListener(ReqHeartbeat.class, reqHeartbeatEventListener);
            netEventDispatcher.removeEventListener(ReqGoodbye.class, reqGoodbyeEventListener);
            netEventDispatcher.removeEventListener(ResWelcome.class, resWelcomeEventListener);
            netEventDispatcher.removeEventListener(ResHeartbeat.class, resHeartbeatEventListener);
            netEventDispatcher.removeEventListener(ResGoodbye.class, resGoodbyeEventListener);
        }
    }

    /**
     * Sends an event on the network.
     * 
     * @param event the event to be sent.
     */
    public void send(Event event) {
        String output = protocol.processOutput(event);
        if (socket.isClosed()) {
            LOGGER.info(String.format("Not sent: %s", output));
            return;
        }
        LOGGER.info(String.format("Sent: %s", output));
        out.println(output);
        out.flush();
    }

    public void setOnClose(Runnable onClose) {
        this.onClose = onClose;
    }

    private void on(ReqWelcome event) {
        send(new ResWelcome());
    }

    private void on(ResWelcome event) {
    }

    private void on(ReqHeartbeat event) {
        send(new ResHeartbeat());
    }

    private void on(ResHeartbeat event) {
    }

    private void on(ReqGoodbye event) {
        send(new ResGoodbye());
        shutdown();
    }

    private void on(ResGoodbye event) {
        shutdown();
    }
}
