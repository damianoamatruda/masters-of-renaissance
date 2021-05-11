package it.polimi.ingsw.server;

import it.polimi.ingsw.common.GameProtocol;
import it.polimi.ingsw.common.events.mvevents.MVEvent;
import it.polimi.ingsw.common.events.mvevents.ResWelcome;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerClientHandler implements Runnable, MVEventSender {
    private final Socket clientSocket;
    private final VirtualView view;
    private final GameProtocol gp;
    private PrintWriter out;
    private BufferedReader in;
    private volatile boolean listening;
    // private final int timeout;

    public ServerClientHandler(Socket clientSocket, VirtualView view, GameProtocol gp) {
        this.clientSocket = clientSocket;

        view.setEventSender(this);
        this.view = view;

        // this.timeout = 10;
        this.gp = gp;

        this.in = null;
        this.out = null;
        this.listening = false;
    }

    public void run() {
        try (
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
        ) {
            this.out = out;
            this.in = in;
            String inputLine;

            view.update(new ResWelcome());

            listening = true;
            while (listening) {
                if ((inputLine = in.readLine()) == null)
                    break;
                try {
                    gp.processInput(inputLine, view);
                } catch (Exception e) {
                    e.printStackTrace();
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

    @Override
    public void stop() {
        listening = false;
    }

    public void send(String output) {
        if (this.out != null)
            out.println(output);
    }

    @Override
    public void send(MVEvent event) {
        send(gp.processOutput(event));
    }
}
