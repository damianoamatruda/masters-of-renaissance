package it.polimi.ingsw.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import it.polimi.ingsw.server.controller.Controller;
import it.polimi.ingsw.server.controller.messages.Message;

import java.net.Socket;

public class GameProtocol {
    private final Controller controller;

    public GameProtocol(Controller controller) {
        this.controller = controller;
    }

    public String processInput(String input, Socket client) {
        /*input = """
                {
                  "type": "ReqDiscardLeader",
                  "leaderId": 1
                }""";*/

        System.out.println("Received: \"" + input + "\"");
        if (input.equals("quit")) return "Bye.";

        /* Interprets command string and calls an action from the model */

        Gson gson = new Gson();
        JsonObject jsonObject;

        try {
            jsonObject = gson.fromJson(input, JsonObject.class);
        } catch (JsonParseException e) {
            System.err.println("Invalid input.");
            return input + "?";
        }

        try {
            Message message = gson.fromJson(jsonObject, Class.forName("it.polimi.ingsw.server.controller.messages." + jsonObject.get("type").getAsString()).asSubclass(Message.class));
            message.handle(controller, client);
            // Prepare response
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return input + "!";
    }
}
