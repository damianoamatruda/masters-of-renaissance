package it.polimi.ingsw.server;

import it.polimi.ingsw.common.NetworkHandler;
import it.polimi.ingsw.common.NetworkProtocol;
import it.polimi.ingsw.common.NetworkProtocolException;
import it.polimi.ingsw.common.events.netevents.ReqGoodbye;
import it.polimi.ingsw.common.events.netevents.ReqHeartbeat;
import it.polimi.ingsw.common.events.netevents.errors.ErrProtocol;
import it.polimi.ingsw.common.events.netevents.errors.ErrRuntime;
import it.polimi.ingsw.common.events.vcevents.ReqQuit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ServerClientHandler extends NetworkHandler {
    private final int timeout;

    public ServerClientHandler(Socket socket, NetworkProtocol protocol) {
        super(socket, protocol);
        this.timeout = 6000000;
    }

    public void run() {
        try (
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            this.out = out;
            this.in = in;
            String inputLine;

            listening = true;
            socket.setSoTimeout(timeout / 2);
            int halfTimeout = 0;
            while (listening) {
                try {
                    if ((inputLine = in.readLine()) == null) {
                        System.out.println("NULL READLINE");
                        dispatch(new ReqQuit());
                        break;
                    }

                    halfTimeout = 0;

                    System.out.println("Received: \"" + inputLine + "\"");

                    try {
                        try {
                            dispatch(protocol.processInputAsNetEvent(inputLine));
                        } catch (NetworkProtocolException e1) {
                            try {
                                dispatch(protocol.processInputAsVCEvent(inputLine));
                            } catch (NetworkProtocolException e2) {
                                e2.printStackTrace();
                                send(new ErrProtocol(e2));
                            }
                        }
                    } catch (Exception e) {
                        send(new ErrRuntime(e));
                        e.printStackTrace();
                    }
                } catch (SocketTimeoutException e) {
                    if (halfTimeout < 1) {
                        send(new ReqHeartbeat());
                        halfTimeout++;
                    } else {
                        e.printStackTrace();
                        send(new ReqGoodbye());
                        dispatch(new ReqQuit());
                        break;
                    }
                }
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
            // System.err.println("Couldn't listen for a connection.");
            // System.err.println(e.getMessage());
            dispatch(new ReqQuit());
        } finally {
            this.out = null;
            this.in = null;
        }
    }
}
