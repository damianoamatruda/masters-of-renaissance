package it.polimi.ingsw.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerClientHandler implements Runnable, NicknameRegister {
    private final Server server;
    private final Socket client;
    private final GameProtocol gp;
    // private final int timeout;

    public ServerClientHandler(Server server, Socket client, GameProtocol gp) {
        this.server = server;
        this.client = client;
        // this.timeout = 10;
        this.gp = gp;
    }

    public void registerNickname(String nickname) {
        server.registerNickname(client, nickname);
    }

    public void run() {
        try {
            PrintWriter out = new PrintWriter(client.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            String inputLine, outputLine;

            out.println("Welcome.");

            while ((inputLine = in.readLine()) != null) {
                String nickname = server.getNickname(client).orElse(null);

                outputLine = null;

                try {
                    outputLine = gp.processInput(inputLine, this, nickname, out);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (outputLine != null) {
                    out.println(outputLine);
                    if (outputLine.equals("Bye.")) {
                        break;
                    }
                }
            }
            in.close();
            out.close();
            client.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
