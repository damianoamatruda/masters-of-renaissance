package it.polimi.ingsw.model;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.*;

import it.polimi.ingsw.JavaResourceTypeFactory;
import it.polimi.ingsw.model.cardrequirements.ResourceRequirement;
import it.polimi.ingsw.model.resourcecontainers.Strongbox;
import it.polimi.ingsw.model.resourcecontainers.Warehouse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.polimi.ingsw.model.devcardcolors.Blue;
import it.polimi.ingsw.model.resourcetypes.*;

/**
 * Test class for DevelopmentCard.
 */
public class DevelopmentCardTest {
    private ResourceTypeFactory resTypeFactory;
    
    // TODO: Add Javadoc
    @BeforeEach
    void setup() {
        resTypeFactory = new JavaResourceTypeFactory();
    }
    
    // TODO: Add Javadoc
    @Test
    void takeFromPlayer() {
        DevelopmentCard card = new DevelopmentCard(Blue.getInstance(), 1,
            new ResourceRequirement(Map.of(resTypeFactory.get("Coin"), 1)), null, 0);

        Player player = new Player("", true, new ArrayList<>(), new Warehouse(0), new Strongbox(), new Production(Map.of(), 0, Map.of(), 0), 0);
        Game game = new Game(List.of(player), new DevCardGrid(List.of(), 0, 0), null, new FaithTrack(Set.of(), Set.of()), 0, 0);
        try { player.getStrongbox().addResource(resTypeFactory.get("Coin")); } catch (Exception e) { }

        assertDoesNotThrow(() -> card.takeFromPlayer(game, player, Map.of(player.getStrongbox(), Map.of(resTypeFactory.get("Coin"), 1))));
    }

    // TODO: Add Javadoc
    @Test
    void takeFromPlayerNotEnoughRes() {
        DevelopmentCard card = new DevelopmentCard(Blue.getInstance(), 1,
            new ResourceRequirement(Map.of(resTypeFactory.get("Coin"), 1)), null, 0);

        Player player = new Player("", false, new ArrayList<>(), new Warehouse(0), new Strongbox(), new Production(Map.of(), 0, Map.of(), 0), 0);
        Game game = new Game(List.of(player), new DevCardGrid(List.of(), 0, 0), null, new FaithTrack(Set.of(), Set.of()), 0, 0);
        try { player.getStrongbox().addResource(resTypeFactory.get("Shield")); } catch (Exception e) { }

        assertThrows(Exception.class, () -> card.takeFromPlayer(game, player, Map.of(player.getStrongbox(), Map.of(resTypeFactory.get("Coin"), 1))));
    }
}
