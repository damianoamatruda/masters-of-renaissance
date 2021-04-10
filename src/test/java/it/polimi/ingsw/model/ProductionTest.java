package it.polimi.ingsw.model;

import it.polimi.ingsw.JavaResourceTypeFactory;
import it.polimi.ingsw.model.resourcecontainers.Strongbox;
import it.polimi.ingsw.model.resourcecontainers.Warehouse;
import it.polimi.ingsw.model.resourcetypes.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Unit test for Production class.
 */
public class ProductionTest {
    private ResourceTypeFactory resTypeFactory;

    @BeforeEach
    void setup() {
        resTypeFactory = new JavaResourceTypeFactory();
    }
    
    /**
     * Tests a general production without blanks.
     */
    @Test
    public void generalProductionWithoutBlanks() {
        Production production = new Production(
                Map.of(resTypeFactory.get("Coin"), 2),
                0,
                Map.of(resTypeFactory.get("Servant"), 3),
                0,
                false);

        Player player = new Player("", false, new ArrayList<>(), new Warehouse(0), new Strongbox(), new Production(Map.of(), 0, Map.of(), 0), 0);
        Game game = new Game(List.of(player), new DevCardGrid(List.of(), 0, 0), null, new FaithTrack(Set.of(), Set.of()), 0, 0);

        try {
            for (int i = 0; i < 6; i++)
                player.getStrongbox().addResource(resTypeFactory.get("Coin"));
        } catch (Exception e) {
            fail();
        }

        try {
            production.activate(game, player, Map.of(), Map.of(),
                    Map.of(player.getStrongbox(), Map.of(resTypeFactory.get("Coin"), 2)),
                    Map.of(player.getStrongbox(), Map.of(resTypeFactory.get("Servant"), 3)));
        } catch (Exception e) {
            fail();
        }

        assertEquals(4, player.getStrongbox().getResourceQuantity(resTypeFactory.get("Coin")));
        assertEquals(3, player.getStrongbox().getResourceQuantity(resTypeFactory.get("Servant")));
    }

    /**
     * Tests a general production with blanks in input.
     */
    @Test
    public void generalProductionWithBlanksInInput() {
        Production production = new Production(
                Map.of(resTypeFactory.get("Coin"), 2),
                3,
                Map.of(resTypeFactory.get("Servant"), 3),
                0,
                false);

        Player player = new Player("", false, new ArrayList<>(), new Warehouse(0), new Strongbox(), new Production(Map.of(), 0, Map.of(), 0), 0);
        Game game = new Game(List.of(player), new DevCardGrid(List.of(), 0, 0), null, new FaithTrack(Set.of(), Set.of()), 0, 0);

        try {
            for (int i = 0; i < 6; i++)
                player.getStrongbox().addResource(resTypeFactory.get("Coin"));
            for (int i = 0; i < 2; i++)
                player.getStrongbox().addResource(resTypeFactory.get("Shield"));
        } catch (Exception e) {
            fail();
        }

        try {
            production.activate(game, player,
                    Map.of(resTypeFactory.get("Coin"), 1,
                            resTypeFactory.get("Shield"), 2),
                    Map.of(),
                    Map.of(player.getStrongbox(), Map.of(
                            resTypeFactory.get("Coin"), 3,      /* 2 + 1 replaced blank */
                            resTypeFactory.get("Shield"), 2)),  /* 0 + 2 replaced blanks */
                    Map.of(player.getStrongbox(), Map.of(
                            resTypeFactory.get("Servant"), 3)));
        } catch (Exception e) {
            fail();
        }

        assertEquals(3, player.getStrongbox().getResourceQuantity(resTypeFactory.get("Coin")));
        assertEquals(0, player.getStrongbox().getResourceQuantity(resTypeFactory.get("Shield")));
        assertEquals(3, player.getStrongbox().getResourceQuantity(resTypeFactory.get("Servant")));
    }

    /**
     * Tests a general production with blanks in output.
     */
    @Test
    public void generalProductionWithBlanksInOutput() {
        Production production = new Production(
                Map.of(resTypeFactory.get("Coin"), 2),
                0,
                Map.of(resTypeFactory.get("Servant"), 3),
                3,
                false);

        Player player = new Player("", false, new ArrayList<>(), new Warehouse(0), new Strongbox(), new Production(Map.of(), 0, Map.of(), 0), 0);
        Game game = new Game(List.of(player), new DevCardGrid(List.of(), 0, 0), null, new FaithTrack(Set.of(), Set.of()), 0, 0);

        try {
            for (int i = 0; i < 6; i++)
                player.getStrongbox().addResource(resTypeFactory.get("Coin"));
        } catch (Exception e) {
            fail();
        }

        try {
            production.activate(game, player,
                    Map.of(),
                    Map.of(resTypeFactory.get("Servant"), 2,
                            resTypeFactory.get("Shield"), 1),
                    Map.of(player.getStrongbox(), Map.of(
                            resTypeFactory.get("Coin"), 2 )),
                    Map.of(player.getStrongbox(), Map.of(
                            resTypeFactory.get("Servant"), 5,   /* 3 + 2 replaced blanks */
                            resTypeFactory.get("Shield"), 1))); /* 0 + 1 replaced blanks */
        } catch (Exception e) {
            fail();
        }

        assertEquals(4, player.getStrongbox().getResourceQuantity(resTypeFactory.get("Coin")));
        assertEquals(5, player.getStrongbox().getResourceQuantity(resTypeFactory.get("Servant")));
        assertEquals(1, player.getStrongbox().getResourceQuantity(resTypeFactory.get("Shield")));
    }
}
