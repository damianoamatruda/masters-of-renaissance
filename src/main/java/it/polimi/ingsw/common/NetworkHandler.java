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

public class NetworkHandler extends AsynchronousEventDispatcher implements Runnable, AutoCloseable {
    private static final Logger LOGGER = Logger.getLogger(NetworkHandler.class.getName());
    private final Socket socket;
    private final NetworkProtocol protocol;
    private final BiFunction<String, NetworkProtocol, Event> processInput;
    private final int timeout;

    private final EventListener<ReqWelcome> reqWelcomeEventListener = this::on;
    private final EventListener<ReqHeartbeat> reqHeartbeatEventListener = this::on;
    private final EventListener<ResWelcome> resWelcomeEventListener = this::on;
    private final EventListener<ResGoodbye> resGoodbyeEventListener = this::on;
    private final EventListener<ResHeartbeat> resHeartbeatEventListener = this::on;
    private final EventListener<ReqGoodbye> reqGoodbyeEventListener = this::on;

    private PrintWriter out;
    private BufferedReader in;
    private volatile boolean listening;
    private Runnable onClose = () -> {
    };

    public NetworkHandler(Socket socket, NetworkProtocol protocol, BiFunction<String, NetworkProtocol, Event> processInput, int timeout) {
        this.socket = socket;
        this.protocol = protocol;
        this.processInput = processInput;
        this.timeout = timeout;
        this.out = null;
        this.in = null;
        this.listening = false;
    }

    @Override
    public void run() {
        this.addEventListener(ReqWelcome.class, reqWelcomeEventListener);
        this.addEventListener(ReqHeartbeat.class, reqHeartbeatEventListener);
        this.addEventListener(ReqGoodbye.class, reqGoodbyeEventListener);
        this.addEventListener(ResWelcome.class, resWelcomeEventListener);
        this.addEventListener(ResHeartbeat.class, resHeartbeatEventListener);
        this.addEventListener(ResGoodbye.class, resGoodbyeEventListener);

        try (
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            this.out = out;
            this.in = in;
            String inputLine;

            socket.setSoTimeout(timeout / 2);
            boolean halfTimeout = false;

            listening = true;
            send(new ReqWelcome());
            while (listening) {
                try {
                    if ((inputLine = in.readLine()) == null) {
                        LOGGER.info("Connection soft-closed by peer");
                        break;
                    }

                    LOGGER.info(inputLine);

                    halfTimeout = false;

                    try {
                        dispatch(protocol.processInputAsNetEvent(inputLine));
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
                } catch (IOException e) {
                    if (!listening) {
                        LOGGER.info("Connection soft-closed by self");
                        break;
                    }
                    throw e;
                }
            }
            if (listening)
                LOGGER.info("Self was still listening");
        } catch (IOException e) {
            LOGGER.log(Level.INFO, "Connection hard-closed", e);
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Unknown runtime exception", e);
        } finally {
            shutdown();
        }
    }

    @Override
    public void close() {
        send(new ReqGoodbye());
    }

    public void shutdown() {
        LOGGER.info("Running NetworkHandler::shutdown"); // TODO: Use LOGGER.entering
        listening = false;
        try {
            socket.close();
        } catch (IOException ignored) {
        }
        out = null;
        in = null;
        onClose.run();
        super.closeNow();
        this.removeEventListener(ReqWelcome.class, reqWelcomeEventListener);
        this.removeEventListener(ReqHeartbeat.class, reqHeartbeatEventListener);
        this.removeEventListener(ReqGoodbye.class, reqGoodbyeEventListener);
        this.removeEventListener(ResWelcome.class, resWelcomeEventListener);
        this.removeEventListener(ResHeartbeat.class, resHeartbeatEventListener);
        this.removeEventListener(ResGoodbye.class, resGoodbyeEventListener);
    }

    public void send(Event event) {
        String output = protocol.processOutput(event);
        if (!listening) {
            LOGGER.info(String.format("Not sent: %s", output));
            return;
        }
        LOGGER.info(String.format("Sent: %s", output));
        out.println(output);
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
