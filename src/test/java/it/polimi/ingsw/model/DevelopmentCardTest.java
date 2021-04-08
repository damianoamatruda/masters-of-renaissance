package it.polimi.ingsw.model;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.*;

import it.polimi.ingsw.model.*;
import org.junit.jupiter.api.Test;

import it.polimi.ingsw.model.devcardcolors.Blue;
import it.polimi.ingsw.model.resourcetypes.*;

/**
 * Test class for DevelopmentCard
 */
public class DevelopmentCardTest {
    @Test
    void takeFromPlayer() {
        DevelopmentCard card = new DevelopmentCard(Blue.getInstance(), 1,
            new ResourceRequirement(Map.of(Coin.getInstance(), 1)), null, 0);

        Game g = new Game(List.of(""), new ArrayList<>(), 0, new ArrayList<>(), 0, 0, new HashMap<>(), 0, 0, 0, 0, 0, OriginalGame.generateVaticanSections(), OriginalGame.generateYellowTiles());
        Player p = g.getPlayers().get(0);
        try { p.getStrongbox().addResource(Coin.getInstance()); } catch (Exception e) { }


        assertDoesNotThrow(() -> card.takeFromPlayer(g, p, Map.of(p.getStrongbox(), Map.of(Coin.getInstance(), 1))));
    }

    @Test
    void takeFromPlayerNotEnoughRes() {
        DevelopmentCard card = new DevelopmentCard(Blue.getInstance(), 1,
            new ResourceRequirement(Map.of(Coin.getInstance(), 1)), null, 0);
        
        Game g = new Game(List.of(""), new ArrayList<>(), 0, new ArrayList<>(), 0, 0, new HashMap<>(), 0, 0, 0, 0, 0, OriginalGame.generateVaticanSections(), OriginalGame.generateYellowTiles());
        Player p = g.getPlayers().get(0);
        try { p.getStrongbox().addResource(Shield.getInstance()); } catch (Exception e) { }

        assertThrows(Exception.class, () -> card.takeFromPlayer(g, p, Map.of(p.getStrongbox(), Map.of(Coin.getInstance(), 1))));
    }
}
