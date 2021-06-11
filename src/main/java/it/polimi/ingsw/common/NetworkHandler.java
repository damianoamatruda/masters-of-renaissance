package it.polimi.ingsw.common;

import it.polimi.ingsw.common.events.Event;
import it.polimi.ingsw.common.events.netevents.*;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

public abstract class NetworkHandler extends AsynchronousEventDispatcher implements Runnable, AutoCloseable {
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
            send(new ReqGoodbye());
            listening = false;
            onClose.run();
        }
        out = null;
        in = null;
    }

    public void send(Event event) {
        if (out != null)
            out.println(protocol.processOutput(event));
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
