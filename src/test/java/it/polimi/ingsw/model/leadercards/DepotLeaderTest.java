package it.polimi.ingsw.model.leadercards;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import it.polimi.ingsw.JavaResourceTypeFactory;
import it.polimi.ingsw.model.Production;
import it.polimi.ingsw.model.resourcecontainers.Strongbox;
import it.polimi.ingsw.model.resourcecontainers.Warehouse;
import it.polimi.ingsw.model.resourcetypes.ResourceTypeFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.polimi.ingsw.model.Player;

import java.util.ArrayList;
import java.util.Map;


/**
 * Test for the DepotLeader class.
 */
public class DepotLeaderTest {
    private ResourceTypeFactory resTypeFactory;

    @BeforeEach
    void setup() {
        resTypeFactory = new JavaResourceTypeFactory();
    }
    
    /**
     * Creation of a DepotLeader with a zero-sized depot.
     */
    @Test
    void getZeroSizeDepot() {
        DepotLeader leader = new DepotLeader(0, resTypeFactory.get("Coin"), null, 0);
        Player p = new Player("", false, new ArrayList<>(), new Warehouse(0), new Strongbox(), new Production(Map.of(), 0, Map.of(), 0), 0);

        try { leader.activate(p); } catch (Exception e) { }

        assertNotNull(leader.getDepot());
    }

    /**
     * Creation of a DepotLeader with 'expected' parameters.
     */
    @Test
    void getDepot() {
        DepotLeader leader = new DepotLeader(1, resTypeFactory.get("Coin"), null, 0);
        Player p = new Player("", false, new ArrayList<>(), new Warehouse(0), new Strongbox(), new Production(Map.of(), 0, Map.of(), 0), 0);

        try { leader.activate(p); } catch (Exception e) { }

        assertNotNull(leader.getDepot());
    }
}
