package it.polimi.ingsw.client;

import it.polimi.ingsw.common.NetworkHandler;
import it.polimi.ingsw.common.NetworkProtocol;
import it.polimi.ingsw.common.NetworkProtocolException;
import it.polimi.ingsw.common.events.mvevents.UpdateServerUnavailable;
import it.polimi.ingsw.common.events.netevents.ReqGoodbye;
import it.polimi.ingsw.common.events.netevents.ReqWelcome;
import it.polimi.ingsw.common.events.netevents.errors.ErrProtocol;
import it.polimi.ingsw.common.events.netevents.errors.ErrRuntime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
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
                if ((inputLine = in.readLine()) == null) {
                    dispatch(new UpdateServerUnavailable());
                    break;
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
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Couldn't listen for a connection", e);
            send(new ReqGoodbye());
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Unknown runtime exception", e);
            send(new ErrRuntime(e));
        } finally {
            close();
        }
    }
}
