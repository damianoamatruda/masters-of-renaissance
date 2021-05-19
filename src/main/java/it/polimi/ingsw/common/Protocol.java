package it.polimi.ingsw.common;

import com.google.gson.*;
import it.polimi.ingsw.common.events.Event;
import it.polimi.ingsw.common.events.mvevents.MVEvent;
import it.polimi.ingsw.common.events.vcevents.VCEvent;

public class Protocol {
    private Gson senderGson = new Gson().newBuilder()
                    .enableComplexMapKeySerialization()
                    .registerTypeHierarchyAdapter(Event.class, (JsonSerializer<Event>) (msg, type, jsonSerializationContext) -> {
                        Gson gson1 = new Gson().newBuilder()
                                .enableComplexMapKeySerialization()
                                .create();
                        JsonElement jsonElement = gson1.toJsonTree(msg);
                        jsonElement.getAsJsonObject().addProperty("type", ((Class<?>) type).getSimpleName());
                        return jsonElement;
                    })
                    .create();
    /*
     * Interprets input string.
     */
    private static <T extends Event> T processInputAs(String input, String packageName, Class<T> eventSuperclass) {
        if (input == null || input.isBlank())
            throw new ProtocolException("Empty input.");

        Gson gson = new Gson();
        JsonObject jsonObject;

        try {
            jsonObject = gson.fromJson(input, JsonObject.class);
        } catch (JsonSyntaxException e) {
            throw new ProtocolException("Invalid syntax.", e);
        }
        if (jsonObject == null)
            throw new ProtocolException("Unknown parser error.");

        JsonElement type = jsonObject.get("type");

        if (type == null)
            throw new ProtocolException("Field \"type\" not found.");

        T event;

        try {
            event = gson.fromJson(jsonObject, Class.forName(String.format("%s.%s", packageName, type.getAsString())).asSubclass(eventSuperclass));
        } catch (ClassNotFoundException e) {
            try {
                event = gson.fromJson(jsonObject, Class.forName(String.format("%s.errors.%s", packageName, type.getAsString())).asSubclass(eventSuperclass));
            } catch (ClassNotFoundException e1) {
                throw new ProtocolException(String.format("Event type \"%s\" as subclass of \"%s\" does not exist.", type.getAsString(), eventSuperclass.getSimpleName()), e1);
            }
        } catch (JsonSyntaxException e) {
            throw new ProtocolException("Invalid attribute types.", e);
        }

        return event;
    }

    public VCEvent processInputAsVCEvent(String input) {
        return processInputAs(input, "it.polimi.ingsw.common.events.vcevents", VCEvent.class);
    }

    public MVEvent processInputAsMVEvent(String input) {
        return processInputAs(input, "it.polimi.ingsw.common.events.mvevents", MVEvent.class);
    }

    public String processOutput(Event event) {
        return senderGson.toJson(event);
    }
}
