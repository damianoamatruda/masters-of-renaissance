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
        controller.joinLobby(client);

        input = """
                {
                  "type": "ReqDiscardLeader",
                  "leaderId": 1
                }""";

        System.out.println("Received: \"" + input + "\"");
        if (input.equals("quit")) return "Bye.";

        /* Interprets command string and calls an action from the model */

        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(input, JsonObject.class);
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
