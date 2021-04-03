package it.polimi.ingsw;

import it.polimi.ingsw.resourcetypes.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Unit test for Production class.
 */
public class ProductionTest {
    /**
     * Tests a general production without blanks.
     */
    @Test
    public void generalProductionWithoutBlanks() {
        Production production = new Production(new HashMap<>(){{
            put(Coin.getInstance(), 2);
        }}, new HashMap<>(){{
            put(Servant.getInstance(), 3);
        }}, false);

        Game game = new Game(new ArrayList<>(){{
            add("player");
        }}, new ArrayList<>(), 0, new ArrayList<>(), 0, 0, new HashMap<>(), 0, 0, 0, 0, 0);
        Player player = game.getPlayers().get(0);
        try {
            player.getStrongbox().addResource(Coin.getInstance());
            player.getStrongbox().addResource(Coin.getInstance());
            player.getStrongbox().addResource(Coin.getInstance());
            player.getStrongbox().addResource(Coin.getInstance());
            player.getStrongbox().addResource(Coin.getInstance());
            player.getStrongbox().addResource(Coin.getInstance());
        } catch (Exception e) {
            fail();
        }

        try {
            production.activate(game, player, new HashMap<>(), new HashMap<>(), new HashMap<>() {{
                put(player.getStrongbox(), new HashMap<>() {{
                    put(Coin.getInstance(), 2);
                }});
            }}, new HashMap<>() {{
                put(player.getStrongbox(), new HashMap<>() {{
                    put(Servant.getInstance(), 3);
                }});
            }});
        } catch (Exception e) {
            fail();
        }

        assertEquals(4, player.getStrongbox().getResourceQuantity(Coin.getInstance()));
        assertEquals(3, player.getStrongbox().getResourceQuantity(Servant.getInstance()));
    }

    /**
     * Tests a general production with blanks in input.
     */
    @Test
    public void generalProductionWithBlanksInInput() {
        Production production = new Production(new HashMap<>(){{
            put(Coin.getInstance(), 2);
            put(Zero.getInstance(), 3);
        }}, new HashMap<>(){{
            put(Servant.getInstance(), 3);
        }}, false);

        Game game = new Game(new ArrayList<>(){{
            add("player");
        }}, new ArrayList<>(), 0, new ArrayList<>(), 0, 0, new HashMap<>(), 0, 0, 0, 0, 0);
        Player player = game.getPlayers().get(0);
        try {
            player.getStrongbox().addResource(Coin.getInstance());
            player.getStrongbox().addResource(Coin.getInstance());
            player.getStrongbox().addResource(Coin.getInstance());
            player.getStrongbox().addResource(Coin.getInstance());
            player.getStrongbox().addResource(Coin.getInstance());
            player.getStrongbox().addResource(Coin.getInstance());
            player.getStrongbox().addResource(Shield.getInstance());
            player.getStrongbox().addResource(Shield.getInstance());
        } catch (Exception e) {
            fail();
        }

        try {
            production.activate(game, player, new HashMap<>(){{
                put(Coin.getInstance(), 1);
                put(Shield.getInstance(), 2);
            }}, new HashMap<>(), new HashMap<>() {{
                put(player.getStrongbox(), new HashMap<>() {{
                    put(Coin.getInstance(), 3); /* 2 + 1 replaced blank */
                    put(Shield.getInstance(), 2); /* 0 + 2 replaced blanks */
                }});
            }}, new HashMap<>() {{
                put(player.getStrongbox(), new HashMap<>() {{
                    put(Servant.getInstance(), 3);
                }});
            }});
        } catch (Exception e) {
            fail();
        }

        assertEquals(3, player.getStrongbox().getResourceQuantity(Coin.getInstance()));
        assertEquals(0, player.getStrongbox().getResourceQuantity(Shield.getInstance()));
        assertEquals(3, player.getStrongbox().getResourceQuantity(Servant.getInstance()));
    }

    /**
     * Tests a general production with blanks in output.
     */
    @Test
    public void generalProductionWithBlanksInOutput() {
        Production production = new Production(new HashMap<>(){{
            put(Coin.getInstance(), 2);
        }}, new HashMap<>(){{
            put(Servant.getInstance(), 3);
            put(Zero.getInstance(), 3);
        }}, false);

        Game game = new Game(new ArrayList<>(){{
            add("player");
        }}, new ArrayList<>(), 0, new ArrayList<>(), 0, 0, new HashMap<>(), 0, 0, 0, 0, 0);
        Player player = game.getPlayers().get(0);
        try {
            player.getStrongbox().addResource(Coin.getInstance());
            player.getStrongbox().addResource(Coin.getInstance());
            player.getStrongbox().addResource(Coin.getInstance());
            player.getStrongbox().addResource(Coin.getInstance());
            player.getStrongbox().addResource(Coin.getInstance());
            player.getStrongbox().addResource(Coin.getInstance());
        } catch (Exception e) {
            fail();
        }

        try {
            production.activate(game, player, new HashMap<>(), new HashMap<>(){{
                put(Servant.getInstance(), 2);
                put(Shield.getInstance(), 1);
            }}, new HashMap<>() {{
                put(player.getStrongbox(), new HashMap<>() {{
                    put(Coin.getInstance(), 2);
                }});
            }}, new HashMap<>() {{
                put(player.getStrongbox(), new HashMap<>() {{
                    put(Servant.getInstance(), 5); /* 3 + 2 */
                    put(Shield.getInstance(), 1); /* 0 + 1 */
                }});
            }});
        } catch (Exception e) {
            fail();
        }

        assertEquals(4, player.getStrongbox().getResourceQuantity(Coin.getInstance()));
        assertEquals(5, player.getStrongbox().getResourceQuantity(Servant.getInstance()));
        assertEquals(1, player.getStrongbox().getResourceQuantity(Shield.getInstance()));
    }
}
