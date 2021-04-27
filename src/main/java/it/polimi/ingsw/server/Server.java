package it.polimi.ingsw.server;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final int port;

    public Server(int port){
        this.port = port;
    }

    public void startServer() {
        ExecutorService executor = Executors.newCachedThreadPool();
        ServerSocket serverSocket;

        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println(e.getMessage()); /* Port unavailable */
            return;
        }
        System.out.println("Server ready");
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                executor.submit(new ServerClientHandler(socket));
            } catch(IOException e) {
                break;
            }
        }
        executor.shutdown();
    }

    public static void main(String[] args) {
        Server server;
        if (args.length == 1)
            server = new Server(Integer.parseInt(args[0]));
        else
            server = new Gson().fromJson(
                    new InputStreamReader(
                            Objects.requireNonNull(Server.class.getResourceAsStream("/server.json"))
                    ), Server.class
            );

        server.startServer();
    }
}
