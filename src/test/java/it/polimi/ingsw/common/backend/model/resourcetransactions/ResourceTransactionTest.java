package it.polimi.ingsw.common.backend.model.resourcetransactions;

import it.polimi.ingsw.common.backend.model.*;
import it.polimi.ingsw.common.backend.model.resourcecontainers.IllegalResourceTransferException;
import it.polimi.ingsw.common.backend.model.resourcecontainers.Strongbox;
import it.polimi.ingsw.common.backend.model.resourcecontainers.Warehouse;
import it.polimi.ingsw.common.backend.model.resourcetypes.IncrementFaithPointsResType;
import it.polimi.ingsw.common.backend.model.resourcetypes.ResourceType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.*;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class for ResourceTransaction.
 */
public class ResourceTransactionTest {
    @Test
    void singleRequestWithoutBlanks() throws IllegalResourceTransferException {
        ResourceType r1 = new ResourceType("r1", true);
        ResourceType r2 = new ResourceType("r2", true);
        Player player = new Player("", false, List.of(), new Warehouse(0), new Strongbox(), new ResourceTransactionRecipe(Map.of(), 0, Map.of(), 0), 0, new PlayerSetup(0, 0, 0, Set.of()));
        Game game = new Game(List.of(player), null, null, List.of(), List.of(), List.of(), List.of(), new DevCardGrid(List.of(), 0, 0), new Market(Map.of(r1, 1), 1, r1), new FaithTrack(Set.of(), Set.of(), 24), 3, 0);
        ResourceTransactionRecipe prod = new ResourceTransactionRecipe(
                Map.of(r1, 2),
                0,
                Set.of(),
                Map.of(r2, 3),
                0,
                Set.of(),
                false
        );

        player.getStrongbox().addResources(Map.of(r1, 6));

        ResourceTransactionRequest pr1 = new ResourceTransactionRequest(
                prod,
                Map.of(),
                Map.of(),
                Map.of(player.getStrongbox(), Map.of(r1, 2)),
                Map.of(player.getStrongbox(), Map.of(r2, 3))
        );
        ResourceTransaction transaction = new ResourceTransaction(List.of(pr1));
        transaction.activate(game, player);

        assertAll("getResourceQuantity",
                () -> assertEquals(4, player.getStrongbox().getResourceQuantity(r1)),
                () -> assertEquals(3, player.getStrongbox().getResourceQuantity(r2))
        );
    }

    @Test
    void singleRequestWithInputBlanks() throws IllegalResourceTransferException {
        ResourceType r1 = new ResourceType("r1", true);
        ResourceType r2 = new ResourceType("r2", true);
        ResourceType r3 = new ResourceType("r3", true);
        Player player = new Player("", false, List.of(), new Warehouse(0), new Strongbox(), new ResourceTransactionRecipe(Map.of(), 0, Map.of(), 0), 0, new PlayerSetup(0, 0, 0, Set.of()));
        Game game = new Game(List.of(player), null, null, List.of(), List.of(), List.of(), List.of(), new DevCardGrid(List.of(), 0, 0), new Market(Map.of(r1, 1), 1, r1), new FaithTrack(Set.of(), Set.of(), 24), 3, 0);
        ResourceTransactionRecipe prod = new ResourceTransactionRecipe(Map.of(r1, 2), 3, Set.of(), Map.of(r2, 3), 0, Set.of(), false);

        player.getStrongbox().addResources(Map.of(r1, 6, r3, 2));

        ResourceTransactionRequest transactionRequest = new ResourceTransactionRequest(
                prod,
                Map.of(r1, 1, r3, 2),
                Map.of(),
                Map.of(player.getStrongbox(), Map.of(r1, 3, r3, 2)),
                Map.of(player.getStrongbox(), Map.of(r2, 3))
        );
        ResourceTransaction transaction = new ResourceTransaction(List.of(transactionRequest));
        transaction.activate(game, player);

        assertAll("getResourceQuantity",
                () -> assertEquals(3, player.getStrongbox().getResourceQuantity(r1)),
                () -> assertEquals(3, player.getStrongbox().getResourceQuantity(r2)),
                () -> assertEquals(0, player.getStrongbox().getResourceQuantity(r3)));
    }

    @Test
    void singleRequestWithOutputBlanks() throws IllegalResourceTransferException {
        ResourceType r1 = new ResourceType("r1", true);
        ResourceType r2 = new ResourceType("r2", true);
        ResourceType r3 = new ResourceType("r3", true);
        Player player = new Player("", false, List.of(), new Warehouse(0), new Strongbox(), new ResourceTransactionRecipe(Map.of(), 0, Map.of(), 0), 0, new PlayerSetup(0, 0, 0, Set.of()));
        Game game = new Game(List.of(player), null, null, List.of(), List.of(), List.of(), List.of(), new DevCardGrid(List.of(), 0, 0), new Market(Map.of(r1, 1), 1, r1), new FaithTrack(Set.of(), Set.of(), 24), 3, 0);
        ResourceTransactionRecipe prod = new ResourceTransactionRecipe(Map.of(r1, 2), 0, Set.of(), Map.of(r2, 3), 3, Set.of(), false);

        player.getStrongbox().addResources(Map.of(r1, 6));

        ResourceTransactionRequest transactionRequest = new ResourceTransactionRequest(
                prod,
                Map.of(),
                Map.of(r2, 2, r3, 1),
                Map.of(player.getStrongbox(), Map.of(r1, 2)),
                Map.of(player.getStrongbox(), Map.of(r2, 5, r3, 1))
        );
        ResourceTransaction transaction = new ResourceTransaction(List.of(transactionRequest));
        transaction.activate(game, player);

        assertAll("getResourceQuantity",
                () -> assertEquals(4, player.getStrongbox().getResourceQuantity(r1)),
                () -> assertEquals(5, player.getStrongbox().getResourceQuantity(r2)),
                () -> assertEquals(1, player.getStrongbox().getResourceQuantity(r3)));
    }
    

    private ResourceType rIn = new ResourceType("rIn", true);
    private ResourceType rOut = new ResourceType("rOut", true);
    private ResourceType rInRepl = new ResourceType("rInRepl", true);
    private ResourceType rOutRepl = new ResourceType("rOutRepl", true);
    private Player pl = new Player("a", false, List.of(), new Warehouse(0), new Strongbox(), new ResourceTransactionRecipe(Map.of(), 0, Map.of(), 0), 0, new PlayerSetup(0, 0, 0, Set.of()));
    private Game g = new Game(List.of(pl), null, null, List.of(), List.of(), List.of(), List.of(), new DevCardGrid(List.of(), 0, 0), new Market(Map.of(rIn, 1), 1, rIn), new FaithTrack(Set.of(), Set.of(), 24), 3, 0);
    
    @Test
    void noReplacementsOrDiscardableOut() {
        ResourceTransactionRecipe recipe = new ResourceTransactionRecipe(Map.of(rIn, 1), 0, Set.of(), Map.of(rOut, 1), 0, Set.of(), false);

        assertDoesNotThrow(() -> pl.getStrongbox().addResources(Map.of(rIn, 1)));

        ResourceTransactionRequest request = new ResourceTransactionRequest(recipe,
            Map.of(), Map.of(),
            Map.of(pl.getStrongbox(), Map.of(rIn, 1)),
            Map.of(pl.getStrongbox(), Map.of(rOut, 1)));
        
        ResourceTransaction transaction = new ResourceTransaction(List.of(request));

        assertDoesNotThrow(() -> transaction.activate(g, pl));
        assertAll(
            () -> assertTrue(() -> pl.getStrongbox().getResourceMap().get(rIn) == null),
            () -> assertTrue(() -> pl.getStrongbox().getResourceMap().get(rOut) == 1),
            () -> assertTrue(pl.getFaithPoints() == 0)
        );
    }

    @Test
    void replacementsNoDiscardableOut() {
        ResourceTransactionRecipe recipe = new ResourceTransactionRecipe(Map.of(rIn, 1), 1, Set.of(), Map.of(rOut, 1), 1, Set.of(), false);

        assertDoesNotThrow(() -> pl.getStrongbox().addResources(Map.of(rIn, 1, rInRepl, 1)));

        ResourceTransactionRequest request = new ResourceTransactionRequest(recipe,
            Map.of(rInRepl, 1), Map.of(rOutRepl, 1),
            Map.of(pl.getStrongbox(), Map.of(rIn, 1, rInRepl, 1)),
            Map.of(pl.getStrongbox(), Map.of(rOut, 1, rOutRepl, 1)));
        
        ResourceTransaction transaction = new ResourceTransaction(List.of(request));

        assertDoesNotThrow(() -> transaction.activate(g, pl));
        assertAll(
            () -> assertTrue(() -> pl.getStrongbox().getResourceMap().get(rIn) == null),
            () -> assertTrue(() -> pl.getStrongbox().getResourceMap().get(rInRepl) == null),
            () -> assertTrue(() -> pl.getStrongbox().getResourceMap().get(rOut) == 1),
            () -> assertTrue(() -> pl.getStrongbox().getResourceMap().get(rOutRepl) == 1),
            () -> assertTrue(pl.getFaithPoints() == 0)
        );
    }

    @Test
    void discardableOut() {
        Player pl2 = new Player("b", false, List.of(), new Warehouse(0), new Strongbox(), new ResourceTransactionRecipe(Map.of(), 0, Map.of(), 0), 0, new PlayerSetup(0, 0, 0, Set.of()));
        Game g2 = new Game(List.of(pl, pl2), null, null, List.of(), List.of(), List.of(), List.of(), new DevCardGrid(List.of(), 0, 0), new Market(Map.of(rIn, 1), 1, rIn), new FaithTrack(Set.of(), Set.of(), 24), 3, 0);

        ResourceTransactionRecipe recipe = new ResourceTransactionRecipe(Map.of(rIn, 1), 0, Set.of(), Map.of(rOut, 1), 0, Set.of(), true);

        assertDoesNotThrow(() -> pl.getStrongbox().addResources(Map.of(rIn, 1)));

        ResourceTransactionRequest request = new ResourceTransactionRequest(recipe,
            Map.of(), Map.of(),
            Map.of(pl.getStrongbox(), Map.of(rIn, 1)),
            Map.of(pl.getStrongbox(), Map.of()));
        
        ResourceTransaction transaction = new ResourceTransaction(List.of(request));

        assertDoesNotThrow(() -> transaction.activate(g2, pl));
        assertAll(
            () -> assertTrue(() -> pl.getStrongbox().getResourceMap().get(rIn) == null),
            () -> assertTrue(() -> pl.getStrongbox().getResourceMap().get(rOut) == null),
            () -> assertTrue(pl.getFaithPoints() == 0),
            () -> assertTrue(pl2.getFaithPoints() == 1)
        );
    }

    @Test
    void replacementsAndDiscardableOut() {
        ResourceTransactionRecipe recipe = new ResourceTransactionRecipe(Map.of(rIn, 1), 1, Set.of(), Map.of(rOut, 1), 1, Set.of(), true);

        assertDoesNotThrow(() -> pl.getStrongbox().addResources(Map.of(rIn, 1, rInRepl, 1)));

        ResourceTransactionRequest request = new ResourceTransactionRequest(recipe,
            Map.of(rInRepl, 1), Map.of(rOutRepl, 1),
            Map.of(pl.getStrongbox(), Map.of(rIn, 1, rInRepl, 1)),
            Map.of(pl.getStrongbox(), Map.of(rOut, 1)));
        
        ResourceTransaction transaction = new ResourceTransaction(List.of(request));

        assertDoesNotThrow(() -> transaction.activate(g, pl));
        assertAll(
            () -> assertTrue(() -> pl.getStrongbox().getResourceMap().get(rIn) == null),
            () -> assertTrue(() -> pl.getStrongbox().getResourceMap().get(rInRepl) == null),
            () -> assertTrue(() -> pl.getStrongbox().getResourceMap().get(rOut) == 1),
            () -> assertTrue(() -> pl.getStrongbox().getResourceMap().get(rOutRepl) == null),
            () -> assertTrue(pl.getFaithPoints() == 0)
        );
    }

    @Test
    void notEnoughInputResources() {
        ResourceTransactionRecipe recipe = new ResourceTransactionRecipe(Map.of(rIn, 1), 1, Set.of(), Map.of(rOut, 1), 1, Set.of(), true);

        assertDoesNotThrow(() -> pl.getStrongbox().addResources(Map.of(rIn, 1)));

        ResourceTransactionRequest request = new ResourceTransactionRequest(recipe,
            Map.of(rInRepl, 1), Map.of(rOutRepl, 1),
            Map.of(pl.getStrongbox(), Map.of(rIn, 1, rInRepl, 1)),
            Map.of(pl.getStrongbox(), Map.of(rOut, 1)));
        
        ResourceTransaction transaction = new ResourceTransaction(List.of(request));

        assertThrows(IllegalResourceTransferException.class, () -> transaction.activate(g, pl));
        assertAll(
            () -> assertTrue(() -> pl.getStrongbox().getResourceMap().get(rIn) == 1),
            () -> assertTrue(() -> pl.getStrongbox().getResourceMap().get(rInRepl) == null),
            () -> assertTrue(() -> pl.getStrongbox().getResourceMap().get(rOut) == null),
            () -> assertTrue(() -> pl.getStrongbox().getResourceMap().get(rOutRepl) == null),
            () -> assertTrue(pl.getFaithPoints() == 0)
        );
    }

    @Test
    void multipleTransactions() {
        ResourceTransactionRecipe recipe = new ResourceTransactionRecipe(Map.of(rIn, 1), 1, Set.of(), Map.of(rOut, 1), 1, Set.of(), false);
        ResourceTransactionRecipe recipe2 = new ResourceTransactionRecipe(Map.of(rInRepl, 1), 0, Set.of(), Map.of(rOut, 1), 0, Set.of(), false);

        assertDoesNotThrow(() -> pl.getStrongbox().addResources(Map.of(rIn, 1, rInRepl, 2)));

        ResourceTransactionRequest request = new ResourceTransactionRequest(recipe,
            Map.of(rInRepl, 1), Map.of(rOutRepl, 1),
            Map.of(pl.getStrongbox(), Map.of(rIn, 1, rInRepl, 1)),
            Map.of(pl.getStrongbox(), Map.of(rOut, 1, rOutRepl, 1)));
        ResourceTransactionRequest request2 = new ResourceTransactionRequest(recipe2,
            Map.of(), Map.of(),
            Map.of(pl.getStrongbox(), Map.of(rInRepl, 1)),
            Map.of(pl.getStrongbox(), Map.of(rOut, 1)));
        
        ResourceTransaction transaction = new ResourceTransaction(List.of(request, request2));

        assertDoesNotThrow(() -> transaction.activate(g, pl));
        assertAll(
            () -> assertTrue(() -> pl.getStrongbox().getResourceMap().get(rIn) == null),
            () -> assertTrue(() -> pl.getStrongbox().getResourceMap().get(rInRepl) == null),
            () -> assertTrue(() -> pl.getStrongbox().getResourceMap().get(rOut) == 2),
            () -> assertTrue(() -> pl.getStrongbox().getResourceMap().get(rOutRepl) == 1),
            () -> assertTrue(pl.getFaithPoints() == 0)
        );
    }
}
