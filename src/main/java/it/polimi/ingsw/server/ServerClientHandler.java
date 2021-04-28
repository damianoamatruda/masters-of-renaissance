package it.polimi.ingsw.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerClientHandler implements Runnable {
    private final Socket socket;
    private final GameProtocol gp;
    // private final int timeout;

    public ServerClientHandler(Socket socket, GameProtocol gp) {
        this.socket = socket;
        // this.timeout = 10;
        this.gp = gp;
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

            outputLine = gp.processInput(null, null);
            out.println(outputLine);

            while ((inputLine = in.readLine()) != null) {
                outputLine = gp.processInput(inputLine, socket);
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
