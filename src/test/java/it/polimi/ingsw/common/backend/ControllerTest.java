package it.polimi.ingsw.common.backend;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializer;
import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.backend.model.FileGameFactory;
import it.polimi.ingsw.common.backend.model.GameFactory;
import it.polimi.ingsw.common.backend.model.Lobby;
import it.polimi.ingsw.common.events.Event;
import it.polimi.ingsw.common.events.mvevents.MVEvent;
import it.polimi.ingsw.server.Server;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;
import java.util.List;

/**
 * Integration tests for the server pipeline.
 */
// @TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ControllerTest {
    /**
     * Sets up a "clean server" scenario.
     */
    @BeforeEach
    void setup() {
        gf = new FileGameFactory(Server.class.getResourceAsStream("/config/config.json"));
        m = new Lobby(gf);
        c = new Controller(m);
        v = new DummyView();
    }

    static class DummyView extends View {
        List<Event> replies;

        public DummyView() {
            replies = new ArrayList<>();
            // setEventPasser(new ListEventPasser(replies));
        }

        public Event getLastMsg() {
            return replies.get(replies.size() - 1);
        }

        /* @Override
        public void on(ResGoodbye event) {
            replies.add(event);
        } */
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

    // static class ListEventPasser implements EventPasser {
    //     List<Event> replies;
    //
    //     public ListEventPasser(List<Event> replies) {
    //         this.replies = replies;
    //     }

    //     @Override
    //     public void on(Event event) {
    //         replies.add(event);
    //     }

    //     @Override
    //     public void stop() {
    //         // TODO Auto-generated method stub

    //     }

    // }
}
