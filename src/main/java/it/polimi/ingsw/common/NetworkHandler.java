package it.polimi.ingsw.common;

import it.polimi.ingsw.common.events.Event;
import it.polimi.ingsw.common.events.netevents.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Logger;

public abstract class NetworkHandler extends AsynchronousEventDispatcher implements Runnable, AutoCloseable {
    private static final Logger LOGGER = Logger.getLogger(NetworkHandler.class.getName());
    protected static final int timeout = 6000000;
    protected final Socket socket;
    protected final NetworkProtocol protocol;
    protected PrintWriter out;
    protected BufferedReader in;
    protected volatile boolean listening;
    protected Runnable onClose = () -> {
    };

    public NetworkHandler(Socket socket, NetworkProtocol protocol) {
        this.addEventListener(ReqGoodbye.class, this::on);
        this.addEventListener(ReqHeartbeat.class, this::on);
        this.addEventListener(ReqWelcome.class, this::on);
        this.addEventListener(ResGoodbye.class, this::on);
        this.addEventListener(ResHeartbeat.class, this::on);
        this.addEventListener(ResWelcome.class, this::on);

        this.socket = socket;
        this.protocol = protocol;
        this.out = null;
        this.in = null;
        this.listening = false;
    }

    public abstract void run();

    public void close() {
        super.close();
        if (listening) {
            listening = false;
            onClose.run();
        }
        send(new ReqGoodbye());
        if (!socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        out = null;
        in = null;
    }

    public void send(Event event) {
        String output = protocol.processOutput(event);
        if (out == null) {
            LOGGER.info(String.format("Not sent: %s", output));
            return;
        }
        LOGGER.info(String.format("Sent: %s", output));
        out.println(output);
    }

    public void setOnClose(Runnable onClose) {
        this.onClose = onClose;
    }

    protected void on(ReqWelcome event) {
        send(new ResWelcome());
    }

    protected void on(ResWelcome event) {
    }

    protected void on(ReqHeartbeat event) {
        send(new ResHeartbeat());
    }

    protected void on(ResHeartbeat event) {
    }

    protected void on(ReqGoodbye event) {
        send(new ResGoodbye());
        close();
    }

    protected void on(ResGoodbye event) {
        close();
    }
}
