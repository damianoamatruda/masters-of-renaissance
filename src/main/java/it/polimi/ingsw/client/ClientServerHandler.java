package it.polimi.ingsw.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import it.polimi.ingsw.common.events.VCEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientServerHandler implements VCEventSender {
    private final static String quitInputType = "ResGoodbye";
    private final static String quitOutputType = "ReqQuit";

    private final String host;
    private final int port;
    private final ExecutorService executor;
    private Socket socket;

    public ClientServerHandler(String host, int port) {
        this.host = host;
        this.port = port;
        this.executor = Executors.newFixedThreadPool(2);
        this.socket = null;
    }

    public void start() throws IOException {
        try {
            socket = new Socket(host, port);
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + host);
            throw e;
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + host + " when creating the socket");
            throw e;
        }

        executor.submit(this::runReceive);
        executor.submit(this::runSend);
    }

    public void stop() {
        executor.shutdown();
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException ignored) {
            }
            socket = null;
        }
    }

    private void runReceive() {
        Gson gson = new Gson();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String fromServer;
            JsonObject jsonObject;
            while ((fromServer = in.readLine()) != null) {
                System.out.println(fromServer);
                try {
                    jsonObject = gson.fromJson(fromServer, JsonObject.class);
                    if (jsonObject != null && jsonObject.get("type") != null && jsonObject.get("type").getAsString().equals(quitInputType)) {
                        System.out.println("[Client] Goodbye message from server. Ending thread 'runReceive'...");
                        break;
                    }
                } catch (JsonSyntaxException ignored) {
                }
            }
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + host + " for input stream");
        }
        stop();
    }

    private void runSend() {
        Gson gson = new Gson();
        try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            String fromClient;
            JsonObject jsonObject;
            while ((fromClient = stdIn.readLine()) != null) {
                out.println(fromClient);
                try {
                    jsonObject = gson.fromJson(fromClient, JsonObject.class);
                    if (jsonObject != null && jsonObject.get("type") != null && jsonObject.get("type").getAsString().equals(quitOutputType)) { /* Necessary as stdIn::readLine is a blocking operation */
                        System.out.println("[Client] Quit message from client. Ending thread 'runSend'...");
                        break;
                    }
                } catch (JsonSyntaxException ignored) {
                }
            }
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + host + " for output stream");
        }
        stop();
    }

    @Override
    public void send(VCEvent event) {
        // TODO: Implement this here or in another class similar to ServerClientHandler
    }
}
