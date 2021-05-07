package it.polimi.ingsw.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import it.polimi.ingsw.client.cli.Cli;
import it.polimi.ingsw.client.gui.Gui;
import it.polimi.ingsw.common.events.VCEvent;

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

public class Client implements VCEventSender {
    private final static String jsonConfigPath = "/server.json";
    private final static String quitInputType = "ResGoodbye";
    private final static String quitOutputType = "ReqQuit";

    private final String host;
    private final int port;
    private final boolean useGui;
    private final ExecutorService executor;
    private Socket socket;

    public Client(String host, int port, boolean useGui) {
        this.host = host;
        this.port = port;
        this.useGui = useGui;
        this.executor = Executors.newFixedThreadPool(2);
        this.socket = null;
    }

    public static void main(String[] args) {
        JsonObject jsonConfig = null;
        Supplier<JsonObject> parseConfig = () -> new Gson().fromJson(new InputStreamReader(Objects.requireNonNull(Client.class.getResourceAsStream(jsonConfigPath))), JsonObject.class);

        String host = args.length >= 1 ? args[0] : (jsonConfig = parseConfig.get()).get("host").getAsString();
        int port = args.length >= 2 ? Integer.parseInt(args[1]) : (jsonConfig != null ? jsonConfig : parseConfig.get()).get("port").getAsInt();
        boolean useGui = args.length < 3 || !args[2].equals("cli");

        try {
            new Client(host, port, useGui).start();
        } catch (IOException e) {
            // System.exit(1);
        }
    }

    public void start() throws IOException {
        System.out.println(useGui ? "Launching GUI..." : "Launching CLI...");
        Ui ui = useGui ? new Gui() : new Cli();
        executor.submit(ui::execute);

        try {
            socket = new Socket(host, port);
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + host);
            throw e;
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + host + " when creating the socket");
            throw e;
        }

        executor.submit(this::receive);
        executor.submit(this::sendT);
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
        Gson gson = new Gson();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String fromServer;
            JsonObject jsonObject;
            while ((fromServer = in.readLine()) != null) {
                System.out.println(fromServer);
                try {
                    jsonObject = gson.fromJson(fromServer, JsonObject.class);
                    if (jsonObject != null && jsonObject.get("type") != null && jsonObject.get("type").getAsString().equals(quitInputType)) {
                        System.out.println("[Client] Goodbye message from server. Ending thread 'receive'...");
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

    private void sendT() {
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
                        System.out.println("[Client] Quit message from client. Ending thread 'send'...");
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
