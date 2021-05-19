package it.polimi.ingsw.server;

import it.polimi.ingsw.common.EventPasser;
import it.polimi.ingsw.common.Protocol;
import it.polimi.ingsw.common.ProtocolException;
import it.polimi.ingsw.common.events.Event;
import it.polimi.ingsw.common.events.mvevents.ErrProtocol;
import it.polimi.ingsw.common.events.mvevents.ErrRuntime;
import it.polimi.ingsw.common.events.mvevents.ResGoodbye;
import it.polimi.ingsw.common.events.mvevents.ResWelcome;
import it.polimi.ingsw.common.events.vcevents.ReqGoodbye;
import it.polimi.ingsw.common.events.mvevents.ReqHeartbeat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ServerClientHandler implements Runnable, EventPasser {
    private final Socket clientSocket;
    private final VirtualView view;
    private final Protocol protocol;
    private PrintWriter out;
    private BufferedReader in;
    private volatile boolean listening;
    private final int timeout;

    public ServerClientHandler(Socket clientSocket, VirtualView view, Protocol protocol) {
        this.clientSocket = clientSocket;

        this.view = view;

        this.timeout = 60000;
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

            view.on(new ResWelcome());

            listening = true;
            clientSocket.setSoTimeout(timeout / 2);
            int halfTimeout = 0;
            while (listening) {
                try {
                    if ((inputLine = in.readLine()) == null)
                        break;
                    try {
                        halfTimeout = 0;

                        System.out.println("Received: \"" + inputLine + "\"");

                        try {
                            view.dispatch(protocol.processInputAsVCEvent(inputLine));
                        } catch (ProtocolException e) {
                            view.on(new ErrProtocol(e));
                        } catch (Exception e) {
                            view.on(new ErrRuntime(e));
                            e.printStackTrace();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (SocketTimeoutException e) {
                    if(halfTimeout < 1) {
                        out.println(protocol.processOutput(new ReqHeartbeat()));
                        halfTimeout++;
                    }
                    else {
                        System.out.println("Heartbeat not received. Dispatching Goodbye.");
                        try {
                            view.dispatch(new ReqGoodbye()); // FIXME: Add type 'NetEvent'
                        } catch (ProtocolException e1) {
                            view.on(new ErrProtocol(e1));
                        } catch (Exception e1) {
                            view.on(new ErrRuntime(e1));
                            e.printStackTrace();
                        }
                        break;
                    }
                }
            }
            System.out.println("Closing socket.");
            clientSocket.close();
        } catch (IOException e) {
            System.err.println("Exception caught when listening for a connection");
            System.err.println(e.getMessage());
        }
        this.out = null;
        this.in = null;
    }

    @Override
    public void stop() {
        listening = false;
    }

    @Override
    public void on(Event event) {
        send(protocol.processOutput(event));
        
        if (event instanceof ResGoodbye)
            stop();
    }

    private void send(String output) {
        if (out != null)
            out.println(output);
    }
}
