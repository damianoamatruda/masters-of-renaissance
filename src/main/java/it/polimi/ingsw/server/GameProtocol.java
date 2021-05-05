package it.polimi.ingsw.server;

import com.google.gson.*;

import it.polimi.ingsw.common.ControllerObservable;
import it.polimi.ingsw.common.ModelObserver;
import it.polimi.ingsw.common.ControllerObservable;
import it.polimi.ingsw.common.events.ErrCommunication;
import it.polimi.ingsw.common.events.ErrController;
import it.polimi.ingsw.common.events.MVEvent;
import it.polimi.ingsw.common.events.VCEvent;

public class GameProtocol {
    // TODO: Evaluate whether to put this class into common

    /*
     * Interprets command string and calls an action from the model.
     */
    public void processInput(String input, ModelObserver view) {
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

        VCEvent event;

        try {
            event = gson.fromJson(jsonObject, Class.forName("it.polimi.ingsw.common.events." + type.getAsString()).asSubclass(VCEvent.class));
        } catch (ClassNotFoundException e) {
            view.update(new ErrCommunication("Event type \"" + type.getAsString() + "\" does not exist."));
            return;
        } catch (JsonSyntaxException e) {
            view.update(new ErrCommunication("Invalid attribute types."));
            return;
        }

        try {
            event.handle((ControllerObservable) view);
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
