package it.polimi.ingsw.model;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.*;

import it.polimi.ingsw.JavaDevCardColorFactory;
import it.polimi.ingsw.JavaResourceTypeFactory;
import it.polimi.ingsw.model.cardrequirements.ResourceRequirement;
import it.polimi.ingsw.model.devcardcolors.DevCardColorFactory;
import it.polimi.ingsw.model.resourcecontainers.Strongbox;
import it.polimi.ingsw.model.resourcecontainers.Warehouse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.polimi.ingsw.model.resourcetypes.*;

/**
 * Test class for DevelopmentCard.
 */
public class DevelopmentCardTest {
    private ResourceTypeFactory resTypeFactory;
    private DevCardColorFactory devCardColorFactory;
    
    /**
     * Test setup to be executed before each test.
     */
    @BeforeEach
    void setup() {
        resTypeFactory = new JavaResourceTypeFactory();
        devCardColorFactory = new JavaDevCardColorFactory();
    }
    
    /**
     * Tests whether taking the resources specified in the card's requirements works if the player satisfies them.
     */
    @Test
    void takeFromPlayer() {
        DevelopmentCard card = new DevelopmentCard(devCardColorFactory.get("Blue"), 1,
            new ResourceRequirement(Map.of(resTypeFactory.get("Coin"), 1)), null, 0);

        Player player = new Player("", true, new ArrayList<>(), new Warehouse(0), new Strongbox(), new Production(Map.of(), 0, Map.of(), 0), 0);
        Game game = new Game(List.of(player), new DevCardGrid(List.of(), 0, 0), null, new FaithTrack(Set.of(), Set.of()), 0, 0);
        player.getStrongbox().addResource(resTypeFactory.get("Coin"));

        assertDoesNotThrow(() -> card.takeFromPlayer(game, player, Map.of(player.getStrongbox(), Map.of(resTypeFactory.get("Coin"), 1))));
    }

    /**
     * Tests whether taking the resources specified in the card's requirements fails if the player does not have enough of the correct type.
     */
    @Test
    void takeFromPlayerNotEnoughRes() {
        DevelopmentCard card = new DevelopmentCard(devCardColorFactory.get("Blue"), 1,
            new ResourceRequirement(Map.of(resTypeFactory.get("Coin"), 1)), null, 0);

        Player player = new Player("", false, new ArrayList<>(), new Warehouse(0), new Strongbox(), new Production(Map.of(), 0, Map.of(), 0), 0);
        Game game = new Game(List.of(player), new DevCardGrid(List.of(), 0, 0), null, new FaithTrack(Set.of(), Set.of()), 0, 0);
        player.getStrongbox().addResource(resTypeFactory.get("Shield"));

        assertThrows(Exception.class, () -> card.takeFromPlayer(game, player, Map.of(player.getStrongbox(), Map.of(resTypeFactory.get("Coin"), 1))));
    }
}
