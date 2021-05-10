package it.polimi.ingsw.common;

import com.google.gson.*;
import it.polimi.ingsw.common.events.ErrUnparsableMessage;
import it.polimi.ingsw.common.events.ErrController;
import it.polimi.ingsw.common.events.MVEvent;
import it.polimi.ingsw.common.events.VCEvent;

public class GameProtocol {
    /*
     * Interprets command string and calls an action from the model.
     */
    public void processInput(String input, View view) {
        if (input == null || input.isBlank()) {
            view.update(new ErrUnparsableMessage("Empty input."));
            return;
        }

        System.out.println("Received: \"" + input + "\"");

        Gson gson = new Gson();
        JsonObject jsonObject;

        try {
            jsonObject = gson.fromJson(input, JsonObject.class);
        } catch (JsonSyntaxException e) {
            view.update(new ErrUnparsableMessage("Invalid syntax."));
            return;
        }
        if (jsonObject == null) {
            view.update(new ErrUnparsableMessage("Unknown parser error."));
            return;
        }

        JsonElement type = jsonObject.get("type");

        if (type == null) {
            view.update(new ErrUnparsableMessage("Field \"type\" not found."));
            return;
        }

        VCEvent event;

        try {
            event = gson.fromJson(jsonObject, Class.forName("it.polimi.ingsw.common.events." + type.getAsString()).asSubclass(VCEvent.class));
        } catch (ClassNotFoundException e) {
            view.update(new ErrUnparsableMessage("Event type \"" + type.getAsString() + "\" does not exist."));
            return;
        } catch (JsonSyntaxException e) {
            view.update(new ErrUnparsableMessage("Invalid attribute types."));
            return;
        }

        try {
            event.handle(view);
        } catch (Exception e) {
            view.update(new ErrController(e));
        }
    }

    public String processOutput(MVEvent event) {
        Gson gson = new Gson().newBuilder()
                .enableComplexMapKeySerialization()
                .registerTypeHierarchyAdapter(MVEvent.class, (JsonSerializer<MVEvent>) (msg, type, jsonSerializationContext) -> {
                    Gson customGson = new Gson().newBuilder()
                            .enableComplexMapKeySerialization()
                            .create();
                    JsonElement jsonElement = customGson.toJsonTree(msg);
                    jsonElement.getAsJsonObject().addProperty("type", ((Class<?>) type).getSimpleName());
                    return jsonElement;
                })
                .create();
        return gson.toJson(event);
    }
}
