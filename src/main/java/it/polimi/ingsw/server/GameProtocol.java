package it.polimi.ingsw.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.polimi.ingsw.server.controller.Controller;
import it.polimi.ingsw.server.controller.messages.Message;

import java.net.Socket;

public class GameProtocol {
    private final Controller controller;

    public GameProtocol(Controller controller) {
        this.controller = controller;
    }

    public String processInput(String input, Socket client) {
        if (input == null) {
            controller.joinLobby(client);
            return "Welcome.";
        }
        System.out.println("Received: \"" + input + "\"");
        if (input.equals("quit")) return "Bye.";

        /* Interprets command string and calls an action from the model */

        String example = """
                {
                  "type": "ReqDiscardLeader",
                  "leaderId": 1
                }""";

        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(/*input*/example, JsonObject.class);
        try {
            Message command = gson.fromJson(jsonObject, Class.forName("it.polimi.ingsw.server.controller.messages." + jsonObject.get("type").getAsString()).asSubclass(Message.class));
            controller.handle(command, client);
            // Prepare response
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return input + "!";
    }
}
