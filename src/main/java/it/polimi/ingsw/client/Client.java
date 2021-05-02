package it.polimi.ingsw.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

public class Client {
    private final static String jsonConfigPath = "/server.json";
    private final static String quitInput = "Bye.";
    private final static String quitOutput = "quit";

    private final String host;
    private final int port;
    private final ExecutorService executor;
    private Socket socket;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
        this.executor = Executors.newFixedThreadPool(2);
        this.socket = null;
    }

    public static void main(String[] args) {
        JsonObject jsonConfig = null;
        Supplier<JsonObject> parseConfig = () -> new Gson().fromJson(new InputStreamReader(Objects.requireNonNull(Client.class.getResourceAsStream(jsonConfigPath))), JsonObject.class);

        String host = args.length >= 1 ? args[0] : (jsonConfig = parseConfig.get()).get("host").getAsString();
        int port = args.length >= 2 ? Integer.parseInt(args[1]) : (jsonConfig != null ? jsonConfig : parseConfig.get()).get("port").getAsInt();

        try {
            new Client(host, port).start();
        } catch (IOException e) {
            System.exit(1);
        }
    }

    public void start() throws IOException {
        try {
            socket = new Socket(host, port);
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + host);
            throw e;
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + host + "when creating the socket");
            throw e;
        }

        executor.submit(this::receive);
        executor.submit(this::send);
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

    private void receive() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String fromServer;
            while ((fromServer = in.readLine()) != null) {
                System.out.println(fromServer);
                if (fromServer.equals(quitInput))
                    break;
            }
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + host + "when creating the input stream");
        }
        stop();
    }

    private void send() {
        try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            String fromClient;
            while ((fromClient = stdIn.readLine()) != null) {
                out.println(fromClient);
                if (fromClient.equals(quitOutput)) /* Necessary as stdIn::readLine is a blocking operation */
                    break;
            }
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + host + "when creating the output stream");
        }
        stop();
    }
}
