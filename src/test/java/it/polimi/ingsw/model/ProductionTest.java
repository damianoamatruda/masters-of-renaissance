package it.polimi.ingsw.model;

import it.polimi.ingsw.model.resourcecontainers.ResourceContainer;
import it.polimi.ingsw.model.resourcetypes.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        Production<ResourceContainer, ResourceContainer> production = new Production<>(
                Map.of(Coin.getInstance(), 2),
                0,
                Map.of(Servant.getInstance(), 3),
                0,
                false);

        Game game = new Game(List.of("player"), new ArrayList<>(), 0, new ArrayList<>(), 0, 0, new HashMap<>(), 0, 0, 0, 0, 0, new HashMap<>(), new HashMap<>());
        Player player = game.getPlayers().get(0);

        try {
            for (int i = 0; i < 6; i++)
                player.getStrongbox().addResource(Coin.getInstance());
        } catch (Exception e) {
            fail();
        }

        try {
            production.activate(game, player, Map.of(), Map.of(),
                    Map.of(player.getStrongbox(), Map.of(Coin.getInstance(), 2)),
                    Map.of(player.getStrongbox(), Map.of(Servant.getInstance(), 3)));
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
        Production<ResourceContainer, ResourceContainer> production = new Production<>(
                Map.of(Coin.getInstance(), 2),
                3,
                Map.of(Servant.getInstance(), 3),
                0,
                false);

        Game game = new Game(List.of("player"), new ArrayList<>(), 0, new ArrayList<>(), 0, 0, new HashMap<>(), 0, 0, 0, 0, 0, new HashMap<>(), new HashMap<>());
        Player player = game.getPlayers().get(0);
        try {
            for (int i = 0; i < 6; i++)
                player.getStrongbox().addResource(Coin.getInstance());
            for (int i = 0; i < 2; i++)
                player.getStrongbox().addResource(Shield.getInstance());
        } catch (Exception e) {
            fail();
        }

        try {
            production.activate(game, player,
                    Map.of(Coin.getInstance(), 1,
                            Shield.getInstance(), 2),
                    Map.of(),
                    Map.of(player.getStrongbox(), Map.of(
                            Coin.getInstance(), 3,      /* 2 + 1 replaced blank */
                            Shield.getInstance(), 2)),  /* 0 + 2 replaced blanks */
                    Map.of(player.getStrongbox(), Map.of(
                            Servant.getInstance(), 3)));
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
        Production<ResourceContainer, ResourceContainer> production = new Production<>(
                Map.of(Coin.getInstance(), 2),
                0,
                Map.of(Servant.getInstance(), 3),
                3,
                false);

        Game game = new Game(List.of("player"), new ArrayList<>(), 0, new ArrayList<>(), 0, 0, new HashMap<>(), 0, 0, 0, 0, 0, new HashMap(), new HashMap<>());
        Player player = game.getPlayers().get(0);
        try {
            for (int i = 0; i < 6; i++)
                player.getStrongbox().addResource(Coin.getInstance());
        } catch (Exception e) {
            fail();
        }

        try {
            production.activate(game, player,
                    Map.of(),
                    Map.of(Servant.getInstance(), 2,
                            Shield.getInstance(), 1),
                    Map.of(player.getStrongbox(), Map.of(
                            Coin.getInstance(), 2 )),
                    Map.of(player.getStrongbox(), Map.of(
                            Servant.getInstance(), 5,   /* 3 + 2 replaced blanks */
                            Shield.getInstance(), 1))); /* 0 + 1 replaced blanks */
        } catch (Exception e) {
            fail();
        }

        assertEquals(4, player.getStrongbox().getResourceQuantity(Coin.getInstance()));
        assertEquals(5, player.getStrongbox().getResourceQuantity(Servant.getInstance()));
        assertEquals(1, player.getStrongbox().getResourceQuantity(Shield.getInstance()));
    }
}
