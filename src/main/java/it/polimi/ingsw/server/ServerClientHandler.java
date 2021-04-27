package it.polimi.ingsw.server;

import it.polimi.ingsw.server.controller.Controller;
import it.polimi.ingsw.server.controller.FileGameFactory;
import it.polimi.ingsw.server.model.GameFactory;
import it.polimi.ingsw.server.model.Lobby;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerClientHandler implements Runnable {
    private final Socket socket;
    private final GameProtocol gp;
    // private final int timeout;

    public ServerClientHandler(Socket socket) {
        this.socket = socket;
        // this.timeout = 10;

        GameFactory gameFactory = new FileGameFactory(getClass().getResourceAsStream("/config.json"));
        Lobby model = new Lobby(gameFactory);
        Controller controller = new Controller(model);
        this.gp = new GameProtocol(controller);
    }

      /* public ServerClientHandler(Socket socket, int timeout) {
          this.socket = socket;
          this.timeout = timeout;
      } */

    public void run() {
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String inputLine, outputLine;

            outputLine = gp.processInput(null);
            out.println(outputLine);

            while ((inputLine = in.readLine()) != null) {
                outputLine = gp.processInput(inputLine);
                out.println(outputLine);
                if (outputLine.equals("Bye.")){
                    break;
                }

            }
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
