package it.polimi.ingsw.server;

import it.polimi.ingsw.common.NetworkHandler;
import it.polimi.ingsw.common.NetworkProtocol;
import it.polimi.ingsw.common.NetworkProtocolException;
import it.polimi.ingsw.common.events.netevents.ReqHeartbeat;
import it.polimi.ingsw.common.events.netevents.errors.ErrProtocol;
import it.polimi.ingsw.common.events.netevents.errors.ErrRuntime;
import it.polimi.ingsw.common.events.vcevents.ReqQuit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerClientHandler extends NetworkHandler {
    private static final Logger LOGGER = Logger.getLogger(ServerClientHandler.class.getName());

    public ServerClientHandler(Socket socket, NetworkProtocol protocol) {
        super(socket, protocol);
    }

    public void run() {
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
            while (listening) {
                try {
                    try {
                        if ((inputLine = in.readLine()) == null) {
                            if (!listening)
                                break;
                            throw new IOException("Null readLine while listening");
                        }
                    } catch (SocketException e) {
                        if (!listening)
                            break;
                        throw e;
                    }

                    LOGGER.info(inputLine);

                    halfTimeout = false;

                    try {
                        dispatch(protocol.processInputAsNetEvent(inputLine));
                    } catch (NetworkProtocolException e1) {
                        try {
                            dispatch(protocol.processInputAsVCEvent(inputLine));
                        } catch (NetworkProtocolException e2) {
                            LOGGER.log(Level.INFO, "Unknown event", e2);
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
            LOGGER.info("Connection soft-closed");
        } catch (IOException e) {
            LOGGER.log(Level.INFO, "Connection hard-closed", e);
            dispatch(new ReqQuit());
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Unknown runtime exception", e);
            send(new ErrRuntime(e));
        } finally {
            shutdown();
        }
    }
}
