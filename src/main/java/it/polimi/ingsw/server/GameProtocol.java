package it.polimi.ingsw.server;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import it.polimi.ingsw.server.controller.Controller;
import it.polimi.ingsw.server.controller.messages.Message;

import java.io.PrintWriter;
import java.net.Socket;

public class GameProtocol {
    private final Controller controller;

    public GameProtocol(Controller controller) {
        this.controller = controller;
    }

    /* Interprets command string and calls an action from the model. */
    public String processInput(String input, Socket client, PrintWriter out) {
        /*input = """
                {"type":"ReqNickname","nickname":"X"}""";*/

        System.out.println("Received: \"" + input + "\"");

        if (input.equals("quit"))
            return "Bye.";

        Gson gson = new Gson();
        JsonObject jsonObject;

        try {
            jsonObject = gson.fromJson(input, JsonObject.class);
        } catch (JsonParseException e) {
            System.err.println("Invalid syntax.");
            return "Invalid syntax.";
        }

        JsonElement type = jsonObject.get("type");
        if (type == null) {
            System.err.println("Field \"type\" not found.");
            return "Field \"type\" not found.";
        }

        Message message;

        try {
            message = gson.fromJson(jsonObject, Class.forName("it.polimi.ingsw.server.controller.messages." + type.getAsString()).asSubclass(Message.class));
        } catch (ClassNotFoundException e) {
            System.err.println("Message type \"" + type.getAsString() + "\" does not exist.");
            return "Invalid message type.";
        } catch (JsonParseException e) {
            System.err.println("Invalid attribute types.");
            return "Invalid attribute types.";
        }

        try {
            message.handle(controller, client, out);
        } catch (Exception e) {
            System.err.println("Controller error.");
            return "Controller error: " + e.getMessage();
        }

        System.out.println("Valid input.");
        return null;
    }
}
