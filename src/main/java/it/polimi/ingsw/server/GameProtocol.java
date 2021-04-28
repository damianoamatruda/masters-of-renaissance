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
        if (input == null) return "Welcome.";
        System.out.println("Received: \"" + input + "\"");
        if (input.equals("quit")) return "Bye.";

        /* Interprets command string and calls an action from the model */

        String example = "{\n" +
                "  \"type\": \"ReqDiscardLeader\",\n" +
                "  \"leader_id\": 0\n" +
                "}";

        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(/*input*/example, JsonObject.class);
        try {
            Message command = gson.fromJson(jsonObject,
                    (Class<? extends Message>) Class.forName("it.polimi.ingsw.server.controller.messages." + jsonObject.get("type").getAsString()));
            command.handle(controller, client);
            // Prepare response

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return input + "!";
    }
}
