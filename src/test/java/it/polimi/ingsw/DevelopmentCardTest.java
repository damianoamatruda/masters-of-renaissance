package it.polimi.ingsw;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.*;

import org.junit.jupiter.api.Test;

import it.polimi.ingsw.devcardcolors.Blue;
import it.polimi.ingsw.resourcetypes.*;

/**
 * Test class for DevelopmentCard
 */
public class DevelopmentCardTest {
    @Test
    void onTaken() {
        DevelopmentCard card = new DevelopmentCard(Blue.getInstance(), 1,
            new ResourceRequirement(Map.of(Coin.getInstance(), 1)), null, 0);

        Game g = new Game(List.of(""), new ArrayList<>(), 0, new ArrayList<>(), 0, 0, new HashMap<>(), 0);
        Player p = g.getPlayers().get(0);
        try { p.getStrongbox().addResource(Coin.getInstance()); } catch (Exception e) { }


        assertDoesNotThrow(() -> card.onTaken(g, p, Map.of(p.getStrongbox(), Map.of(Coin.getInstance(), 1))));
    }

    @Test
    void onTakenNotEnoughRes() {
        DevelopmentCard card = new DevelopmentCard(Blue.getInstance(), 1,
            new ResourceRequirement(Map.of(Coin.getInstance(), 1)), null, 0);
        
        Game g = new Game(List.of(""), new ArrayList<>(), 0, new ArrayList<>(), 0, 0, new HashMap<>(), 0);
        Player p = g.getPlayers().get(0);
        try { p.getStrongbox().addResource(Shield.getInstance()); } catch (Exception e) { }

        assertThrows(Exception.class, () -> card.onTaken(g, p, Map.of(p.getStrongbox(), Map.of(Coin.getInstance(), 1))));
    }
}
