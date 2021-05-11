package it.polimi.ingsw.server;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializer;
import it.polimi.ingsw.common.backend.Controller;
import it.polimi.ingsw.common.backend.model.FileGameFactory;
import it.polimi.ingsw.common.backend.model.GameFactory;
import it.polimi.ingsw.common.backend.model.Lobby;
import it.polimi.ingsw.common.events.mvevents.MVEvent;
import it.polimi.ingsw.common.events.mvevents.ResGoodbye;
import it.polimi.ingsw.common.events.mvevents.ResJoin;
import it.polimi.ingsw.common.events.mvevents.UpdateFreeSeats;
import it.polimi.ingsw.common.events.vcevents.ReqJoin;
import it.polimi.ingsw.common.events.vcevents.ReqNewGame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


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

        MVEvent lboMsg = v.replies.get(v.replies.size() - 2);
        assertTrue(lboMsg instanceof UpdateFreeSeats);
        assertEquals(1, ((UpdateFreeSeats) lboMsg).getCountToNewGame());
        assertEquals(0, ((UpdateFreeSeats) lboMsg).getFreeSeats());

        String resGameStarted = gson.toJson(v.getLastMsg());
        int x = 0;
    }
}
