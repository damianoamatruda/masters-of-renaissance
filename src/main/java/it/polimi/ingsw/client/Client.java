package it.polimi.ingsw.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    String hostName;
    int portNumber;

    public Client(String hostName, int portNumber){
        this.hostName = hostName;
        this.portNumber = portNumber;
    }

    public static void main(String[] args) {
        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);
        new Client(hostName, portNumber).executeClient();
    }

    public void executeClient(){
        try (
                Socket gameSocket = new Socket(hostName, portNumber);
                PrintWriter out =
                        new PrintWriter(gameSocket.getOutputStream(), true);
                BufferedReader in =
                        new BufferedReader(
                                new InputStreamReader(gameSocket.getInputStream()));
                BufferedReader stdIn =
                        new BufferedReader(
                                new InputStreamReader(System.in))
        ) {
            String fromServer;

            while((fromServer = in.readLine()) != null){
                System.out.println("Server: " + fromServer);
                if(fromServer.equals("Bye."))
                    break;

                String fromUser = stdIn.readLine();
                if(fromUser != null){
                    System.out.println("Client: " + fromUser);
                    out.println(fromUser);
                }
            }

        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                    hostName);
            System.exit(1);
        }
    }
}

