package it.polimi.ingsw.client;

import it.polimi.ingsw.common.NetworkHandler;
import it.polimi.ingsw.common.NetworkProtocol;
import it.polimi.ingsw.common.NetworkProtocolException;
import it.polimi.ingsw.common.events.mvevents.UpdateServerUnavailable;
import it.polimi.ingsw.common.events.netevents.ReqWelcome;
import it.polimi.ingsw.common.events.netevents.errors.ErrProtocol;
import it.polimi.ingsw.common.events.netevents.errors.ErrRuntime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientServerHandler extends NetworkHandler {
    private static final Logger LOGGER = Logger.getLogger(ClientServerHandler.class.getName());

    public ClientServerHandler(Socket socket, NetworkProtocol protocol) {
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

            send(new ReqWelcome());

            listening = true;
            while (listening) {
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

                try {
                    dispatch(protocol.processInputAsNetEvent(inputLine));
                } catch (NetworkProtocolException e1) {
                    try {
                        dispatch(protocol.processInputAsMVEvent(inputLine));
                    } catch (NetworkProtocolException e2) {
                        send(new ErrProtocol(e2));
                    }
                }
            }
            LOGGER.info("Connection soft-closed");
        } catch (IOException e) {
            LOGGER.log(Level.INFO, "Connection hard-closed", e);
            dispatch(new UpdateServerUnavailable());
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Unknown runtime exception", e);
            send(new ErrRuntime(e));
        } finally {
            shutdown();
        }
    }
}
