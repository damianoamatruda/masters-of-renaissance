package it.polimi.ingsw.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerClientHandler implements Runnable, NicknameRegister {
    private final Server server;
    private final Socket clientSocket;
    private final GameProtocol gp;
    // private final int timeout;

    public ServerClientHandler(Server server, Socket clientSocket, GameProtocol gp) {
        this.server = server;
        this.clientSocket = clientSocket;
        // this.timeout = 10;
        this.gp = gp;
    }

    public void registerNickname(String nickname) {
        server.registerNickname(clientSocket, nickname);
    }

    public void run() {
        try (
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                                clientSocket.getInputStream()))
        ) {
            String inputLine;

            out.println("Welcome.");

            while ((inputLine = in.readLine()) != null) {
                String nickname = server.getNickname(clientSocket).orElse(null);
                boolean status = false;
                try {
                    status = gp.processInput(inputLine, this, nickname, out);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (!status)
                    break;
            }
            clientSocket.close();
        } catch (IOException e) {
            System.err.println("Exception caught when listening for a connection");
            System.err.println(e.getMessage());
        }
    }
}
