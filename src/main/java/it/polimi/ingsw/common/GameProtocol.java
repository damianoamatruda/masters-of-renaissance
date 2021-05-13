package it.polimi.ingsw.common;

import com.google.gson.*;
import it.polimi.ingsw.common.events.mvevents.ErrProtocol;
import it.polimi.ingsw.common.events.mvevents.ErrRuntime;
import it.polimi.ingsw.common.events.mvevents.MVEvent;
import it.polimi.ingsw.common.events.vcevents.VCEvent;

public class GameProtocol {
    /*
     * Interprets command string and calls an action from the model.
     */
    public void processInput(String input, View view) {
        if (input == null || input.isBlank()) {
            view.update(new ErrProtocol("Empty input."));
            return;
        }

        System.out.println("Received: \"" + input + "\"");

        Gson gson = new Gson();
        JsonObject jsonObject;

        try {
            jsonObject = gson.fromJson(input, JsonObject.class);
        } catch (JsonSyntaxException e) {
            view.update(new ErrProtocol("Invalid syntax."));
            return;
        }
        if (jsonObject == null) {
            view.update(new ErrProtocol("Unknown parser error."));
            return;
        }

        JsonElement type = jsonObject.get("type");

        if (type == null) {
            view.update(new ErrProtocol("Field \"type\" not found."));
            return;
        }

        VCEvent event;

        try {
            event = gson.fromJson(jsonObject, Class.forName("it.polimi.ingsw.common.events.vcevents." + type.getAsString()).asSubclass(VCEvent.class));
        } catch (ClassNotFoundException e) {
            view.update(new ErrProtocol("Event type \"" + type.getAsString() + "\" does not exist."));
            return;
        } catch (JsonSyntaxException e) {
            view.update(new ErrProtocol("Invalid attribute types."));
            return;
        }

        try {
            event.handle(view);
        } catch (Exception e) {
            view.update(new ErrRuntime(e));
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
