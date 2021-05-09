package it.polimi.ingsw.server;

import static org.junit.jupiter.api.Assertions.assertTrue;

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
        MVEvent lastReply;

        public DummyView(Controller controller) {
            super(controller);
        }

        @Override
        public void update(ResGoodbye event) {
            lastReply = event;
        }

        @Override
        public void update(MVEvent event) {
            lastReply = event;
        }
    }

    private GameFactory gf;
    private Lobby m;
    private Controller c;
    private DummyView v;

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
        assertTrue(v.lastReply instanceof ResJoin);
        assertTrue(((ResJoin)v.lastReply).isFirst());
    }

    @Test
    void newGame() {
        gameJoin();
        v.notify(new ReqNewGame(1));
        assertTrue(v.lastReply instanceof ResNewGame);
        assertTrue(((ResNewGame)v.lastReply).getCount() == 1);
    }

}
