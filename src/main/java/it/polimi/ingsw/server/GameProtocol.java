package it.polimi.ingsw.server;

import com.google.gson.*;
import it.polimi.ingsw.server.controller.messages.ErrCommunication;
import it.polimi.ingsw.server.controller.messages.ErrController;
import it.polimi.ingsw.server.controller.messages.Message;
import it.polimi.ingsw.server.view.View;

public class GameProtocol {
    /*
     * Interprets command string and calls an action from the model.
     */
    public void processInput(String input, View view) {
        if (input == null || input.isBlank()) {
            view.update(new ErrCommunication("Empty input."));
            return;
        }

        System.out.println("Received: \"" + input + "\"");

        Gson gson = new Gson();
        JsonObject jsonObject;

        try {
            jsonObject = gson.fromJson(input, JsonObject.class);
        } catch (JsonSyntaxException e) {
            view.update(new ErrCommunication("Invalid syntax."));
            return;
        }
        if (jsonObject == null) {
            view.update(new ErrCommunication("Unknown parser error."));
            return;
        }

        JsonElement type = jsonObject.get("type");

        if (type == null) {
            view.update(new ErrCommunication("Field \"type\" not found."));
            return;
        }

        Message message;

        try {
            message = gson.fromJson(jsonObject, Class.forName("it.polimi.ingsw.server.controller.messages." + type.getAsString()).asSubclass(Message.class));
        } catch (ClassNotFoundException e) {
            view.update(new ErrCommunication("Message type \"" + type.getAsString() + "\" does not exist."));
            return;
        } catch (JsonSyntaxException e) {
            view.update(new ErrCommunication("Invalid attribute types."));
            return;
        }

        try {
            message.handle(view);
        } catch (Exception e) {
            view.update(new ErrController(e));
        }
    }

    public String processOutput(Message message) {
        Gson gson = new Gson().newBuilder()
                .enableComplexMapKeySerialization()
                .registerTypeHierarchyAdapter(Message.class, (JsonSerializer<Message>) (msg, type, jsonSerializationContext) -> {
                    Gson customGson = new Gson().newBuilder()
                            .enableComplexMapKeySerialization()
                            .create();
                    JsonElement jsonElement = customGson.toJsonTree(msg);
                    jsonElement.getAsJsonObject().addProperty("type", ((Class<?>) type).getSimpleName());
                    return jsonElement;
                })
                .create();
        return gson.toJson(message);
    }
}
