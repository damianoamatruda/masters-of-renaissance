package it.polimi.ingsw.server;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonElement;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import it.polimi.ingsw.common.events.MVEvent;
import it.polimi.ingsw.common.events.*;
import it.polimi.ingsw.server.model.FileGameFactory;
import it.polimi.ingsw.server.model.GameFactory;
import it.polimi.ingsw.server.model.Lobby;


/**
 * Integration tests for the server pipeline.
 */
// @TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ServerPipelineTest {
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
    private Gson gson = new Gson().newBuilder()
        .enableComplexMapKeySerialization()
        .registerTypeHierarchyAdapter(MVEvent.class, (JsonSerializer<MVEvent>) (msg, type, jsonSerializationContext) -> {
            Gson customGson = new Gson().newBuilder()
                    .enableComplexMapKeySerialization().setPrettyPrinting()
                    .create();
            JsonElement jsonElement = customGson.toJsonTree(msg);
            jsonElement.getAsJsonObject().addProperty("type", ((Class<?>) type).getSimpleName());
            return jsonElement; })
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
    void gameJoin() {
        v.notify(new ReqJoin("Marco"));
        assertTrue(v.getLastMsg() instanceof ResJoin);
        assertTrue(((ResJoin)v.getLastMsg()).isFirst());
        v.replies.remove(v.getLastMsg());
    }

    @Test
    void newGame() {
        gameJoin();
        v.notify(new ReqNewGame(1));

        MVEvent lboMsg = v.replies.get(v.replies.size()-2);
        assertTrue(lboMsg instanceof ResNewGame);
        assertTrue(((ResNewGame)lboMsg).getCount() == 1);

        String resGameStarted = gson.toJson(v.getLastMsg());
        int x = 0;
    }

}
