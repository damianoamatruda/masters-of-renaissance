package it.polimi.ingsw.server;

import it.polimi.ingsw.common.EventPasser;
import it.polimi.ingsw.common.Protocol;
import it.polimi.ingsw.common.events.Event;
import it.polimi.ingsw.common.events.mvevents.ResWelcome;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ServerClientHandler /* extends EventEmitter */ implements Runnable, EventPasser {
    private final Socket clientSocket;
    private final VirtualView view;
    private final Protocol protocol;
    private PrintWriter out;
    private BufferedReader in;
    private volatile boolean listening;
    private final int timeout;

    public ServerClientHandler(Socket clientSocket, VirtualView view, Protocol protocol) {
        // super(Set.of(ResWelcome.class));

        this.clientSocket = clientSocket;

        this.view = view;

        this.timeout = 600000;
        this.protocol = protocol;

        this.in = null;
        this.out = null;
        this.listening = false;

        this.view.setEventPasser(this);
    }

    public void run() {
        try (
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
        ) {
            this.out = out;
            this.in = in;
            String inputLine;
            String reqGoodbye = "{\"type\":\"ReqGoodbye\"}";

            // emit(new ResWelcome());
            on(new ResWelcome());

            listening = true;
            clientSocket.setSoTimeout(timeout / 2);
            int halfTimeout = 0;
            while (listening) {
                try {
                    if ((inputLine = in.readLine()) == null)
                        break;
                    try {
                        halfTimeout = 0;
                        protocol.processInput(inputLine, view);
                        if (inputLine.equals(reqGoodbye))
                            break;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (SocketTimeoutException e) {
                    if(halfTimeout < 1) {
                        out.println("{\"type\":\"ReqHeartbeat\"}");
                        halfTimeout++;
                    }
                    else {
                        protocol.processInput(reqGoodbye, view);
                        break;
                    }
                }
            }
            clientSocket.close();
        } catch (IOException e) {
            System.err.println("Exception caught when listening for a connection");
            System.err.println(e.getMessage());
        }
        this.out = null;
        this.in = null;
    }

    public void stop() {
        listening = false;
    }

    private void send(String output) {
        if (this.out != null)
            out.println(output);
    }

    @Override
    public void on(Event event) {
        send(protocol.processOutput(event));
    }
}
