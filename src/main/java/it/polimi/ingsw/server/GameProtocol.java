package it.polimi.ingsw.server;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import it.polimi.ingsw.server.controller.Controller;
import it.polimi.ingsw.server.controller.messages.Message;

import java.io.PrintWriter;

public class GameProtocol {
    private final Controller controller;

    public GameProtocol(Controller controller) {
        this.controller = controller;
    }

    /* Interprets command string and calls an action from the model. */
    public boolean processInput(String input, NicknameRegister nicknameRegister, String nickname, PrintWriter out) {
        if (input == null || input.isBlank()) {
            log(out, "Empty input.");
            return true;
        }

        System.out.println("Received: \"" + input + "\"");

        if (input.equals("quit")) {
            log(out, "Bye.");
            return false;
        }

        Gson gson = new Gson();
        JsonObject jsonObject;
        try {
            jsonObject = gson.fromJson(input, JsonObject.class);
        } catch (JsonSyntaxException e) {
            log(out, "Invalid syntax.");
            return true;
        }
        if (jsonObject == null) {
            log(out, "Unknown parser error.");
            return true;
        }

        JsonElement type = jsonObject.get("type");
        if (type == null) {
            log(out, "Field \"type\" not found.");
            return true;
        }

        Message message;

        try {
            message = gson.fromJson(jsonObject, Class.forName("it.polimi.ingsw.server.controller.messages." + type.getAsString()).asSubclass(Message.class));
        } catch (ClassNotFoundException e) {
            log(out, "Message type \"" + type.getAsString() + "\" does not exist.");
            return true;
        } catch (JsonSyntaxException e) {
            log(out, "Invalid attribute types.");
            return true;
        }

        try {
            message.handle(controller, nicknameRegister, nickname, out);
        } catch (Exception e) {
            log(out, "Controller error: " + e.getMessage());
            return true;
        }

        System.out.println("Valid input.");
        return true;
    }

    private void log(PrintWriter out, String message) {
        System.err.println(message);
        out.println(message);
    }
}
