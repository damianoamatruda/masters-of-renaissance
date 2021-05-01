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
import java.util.function.Supplier;

public class Client {
    private final static String jsonConfigPath = "/server.json";

    private final String host;
    private final int port;
    private final Socket gameSocket;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
        Socket gameSocket = null;
        try {
            gameSocket = new Socket(host, port);
        } catch (UnknownHostException e) {
            hostError();
        } catch (IOException e) {
            ioError();
        }
        this.gameSocket = gameSocket;
    }

    public static void main(String[] args) {
        JsonObject jsonConfig = null;
        Supplier<JsonObject> parseConfig = () -> new Gson().fromJson(new InputStreamReader(Objects.requireNonNull(Client.class.getResourceAsStream(jsonConfigPath))), JsonObject.class);

        String hostName = args.length >= 1 ? args[0] : (jsonConfig = parseConfig.get()).get("host").getAsString();
        int portNumber = args.length >= 2 ? Integer.parseInt(args[1]) : (jsonConfig != null ? jsonConfig : parseConfig.get()).get("port").getAsInt();

        new Client(hostName, portNumber).start();
    }

    public void start() {
        new Thread(this::receive).start();
        new Thread(this::send).start();
    }

    private void receive() {
        try (
                BufferedReader in =
                        new BufferedReader(
                                new InputStreamReader(gameSocket.getInputStream()))
        ) {
            String fromServer;
            while ((fromServer = in.readLine()) != null) {
                System.out.println("Server: " + fromServer);
                if (fromServer.equals("Bye."))
                    break;
            }
        } catch (IOException e) {
            ioError();
        }
    }

    private void send() {
        try (
                PrintWriter out =
                        new PrintWriter(gameSocket.getOutputStream(), true);
                BufferedReader stdIn =
                        new BufferedReader(
                                new InputStreamReader(System.in))
        ) {
            String fromUser;
            while ((fromUser = stdIn.readLine()) != null) {
                System.out.println("Client: " + fromUser);
                out.println(fromUser);
            }
        } catch (IOException e) {
            ioError();
        }
    }

    private void hostError() {
        System.err.println("Don't know about host " + host);
        System.exit(1);
    }

    private void ioError() {
        System.err.println("Couldn't get I/O for the connection to " +
                host);
        System.exit(1);
    }
}
