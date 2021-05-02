package it.polimi.ingsw.server;

import com.google.gson.*;
import it.polimi.ingsw.server.controller.messages.Message;
import it.polimi.ingsw.server.view.View;

public class GameProtocol {
    /*
     * Interprets command string and calls an action from the model.
     */
    public void processInput(String input, View view) {
        if (input == null || input.isBlank()) {
            view.updateEmptyInput();
            return;
        }

        System.out.println("Received: \"" + input + "\"");

        Gson gson = new Gson();
        JsonObject jsonObject;

        try {
            jsonObject = gson.fromJson(input, JsonObject.class);
        } catch (JsonSyntaxException e) {
            view.updateInvalidSyntax();
            return;
        }
        if (jsonObject == null) {
            view.updateUnknownParserError();
            return;
        }

        JsonElement type = jsonObject.get("type");

        if (type == null) {
            view.updateFieldTypeNotFound();
            return;
        }

        Message message;

        try {
            message = gson.fromJson(jsonObject, Class.forName("it.polimi.ingsw.server.controller.messages." + type.getAsString()).asSubclass(Message.class));
        } catch (ClassNotFoundException e) {
            view.updateMessageTypeDoesNotExist(type.getAsString());
            return;
        } catch (JsonSyntaxException e) {
            view.updateInvalidAttributeTypes();
            return;
        }

        try {
            message.handle(view);
        } catch (Exception e) {
            view.updateControllerError(e.getMessage());
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
