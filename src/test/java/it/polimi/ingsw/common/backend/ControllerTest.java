package it.polimi.ingsw.common.backend;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializer;
import it.polimi.ingsw.common.backend.model.FileGameFactory;
import it.polimi.ingsw.common.backend.model.GameFactory;
import it.polimi.ingsw.common.backend.model.Lobby;
import it.polimi.ingsw.common.events.mvevents.*;
import it.polimi.ingsw.common.events.vcevents.*;
import it.polimi.ingsw.server.MVEventSender;
import it.polimi.ingsw.server.Server;
import it.polimi.ingsw.server.VirtualView;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Integration tests for the server pipeline.
 */
// @TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ControllerTest {
    class DummyView extends VirtualView {
        List<MVEvent> replies;

        public DummyView(Controller controller) {
            super(controller);
            replies = new ArrayList<>();
            this.eventSender = new ListEventSender(replies);
        }

        public MVEvent getLastMsg() {
            return replies.get(replies.size() - 1);
        }

        @Override
        public void update(ResGoodbye event) {
            replies.add(event);
        }
    }

    class ListEventSender implements MVEventSender {
        List<MVEvent> replies;
        public ListEventSender(List<MVEvent> replies) { this.replies = replies; }
        @Override
        public void send(MVEvent event) {
            replies.add(event);
        }

        @Override
        public void stop() {
            // TODO Auto-generated method stub
            
        }

    }

    private GameFactory gf;
    private Lobby m;
    private Controller c;
    private DummyView v;
    private final Gson gson = new Gson().newBuilder()
            .enableComplexMapKeySerialization()
            .registerTypeHierarchyAdapter(MVEvent.class, (JsonSerializer<MVEvent>) (msg, type, jsonSerializationContext) -> {
                Gson customGson = new Gson().newBuilder()
                        .enableComplexMapKeySerialization().setPrettyPrinting()
                        .create();
                JsonElement jsonElement = customGson.toJsonTree(msg);
                jsonElement.getAsJsonObject().addProperty("type", ((Class<?>) type).getSimpleName());
                return jsonElement;
            })
            .setPrettyPrinting().create();
    /**
     * Sets up a "clean server" scenario.
     */
    @BeforeEach
    void setup() {
        gf = new FileGameFactory(Server.class.getResourceAsStream("/config/config.json"));
        m = new Lobby(gf);
        c = new Controller(m);
        v = new DummyView(c);
    }
}