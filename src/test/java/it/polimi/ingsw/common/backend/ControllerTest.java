package it.polimi.ingsw.common.backend;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializer;
import it.polimi.ingsw.common.backend.model.FileGameFactory;
import it.polimi.ingsw.common.backend.model.GameFactory;
import it.polimi.ingsw.common.backend.model.Lobby;
import it.polimi.ingsw.common.events.mvevents.*;
import it.polimi.ingsw.common.events.vcevents.*;
import it.polimi.ingsw.server.Server;
import it.polimi.ingsw.server.VirtualView;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

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
        }

        public MVEvent getLastMsg() {
            return replies.get(replies.size() - 1);
        }

        @Override
        public void update(ResGoodbye event) {
            replies.add(event);
        }

        @Override
        public void update(MVEvent event) {
            replies.add(event);
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

    @Test
    void newGame() {
        DummyView v2 = new DummyView(c);
        DummyView v3 = new DummyView(c);
        DummyView v4 = new DummyView(c);
        
        v.notify(new ReqJoin("Marco"));
        v2.notify(new ReqJoin("Damiano"));
        v3.notify(new ReqJoin("Alessandro"));
        v4.notify(new ReqJoin("p4"));
        v.notify(new ReqNewGame(2));
        
        v2.notify(new ReqNewGame(2));

        String resGameStarted = gson.toJson(v.replies.get(3));
        // String resGameStarted2 = gson.toJson(v2.replies.get(0));
        // v.notify(new GoodBye());
        // v.notify(new ReqJoin("Marco"));

        int y = 0;
        int x = 0;
    }
}
