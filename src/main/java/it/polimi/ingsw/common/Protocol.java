package it.polimi.ingsw.common;

import com.google.gson.*;
import it.polimi.ingsw.common.events.Event;
import it.polimi.ingsw.common.events.mvevents.ErrProtocol;
import it.polimi.ingsw.common.events.mvevents.ErrRuntime;
import it.polimi.ingsw.common.events.mvevents.MVEvent;
import it.polimi.ingsw.common.events.vcevents.VCEvent;

import java.util.Set;

public class Protocol extends EventEmitter {
    public Protocol() {
        super(Set.of(ErrProtocol.class, ErrRuntime.class));
    }

    /*
     * Interprets command string and calls an action from the model.
     */
    public void processInput(String input, View view) {
        // TODO: Implement private events in some way

        if (input == null || input.isBlank()) {
            emit(new ErrProtocol("Empty input."));
            return;
        }

        System.out.println("Received: \"" + input + "\"");

        Gson gson = new Gson();
        JsonObject jsonObject;

        try {
            jsonObject = gson.fromJson(input, JsonObject.class);
        } catch (JsonSyntaxException e) {
            emit(new ErrProtocol("Invalid syntax."));
            return;
        }
        if (jsonObject == null) {
            emit(new ErrProtocol("Unknown parser error."));
            return;
        }

        JsonElement type = jsonObject.get("type");

        if (type == null) {
            emit(new ErrProtocol("Field \"type\" not found."));
            return;
        }

        VCEvent event;

        try {
            event = gson.fromJson(jsonObject, Class.forName("it.polimi.ingsw.common.events.vcevents." + type.getAsString()).asSubclass(VCEvent.class));
        } catch (ClassNotFoundException e) {
            emit(new ErrProtocol("MVEvent type \"" + type.getAsString() + "\" does not exist."));
            return;
        } catch (JsonSyntaxException e) {
            emit(new ErrProtocol("Invalid attribute types."));
            return;
        }

        try {
            event.handle(view);
        } catch (Exception e) {
            emit(new ErrRuntime(e));
        }
    }

    public String processOutput(Event event) {
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
