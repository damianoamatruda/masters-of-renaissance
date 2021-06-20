package it.polimi.ingsw.common.backend.model.resourcetransactions;

import it.polimi.ingsw.common.backend.model.resourcecontainers.Shelf;
import it.polimi.ingsw.common.backend.model.resourcetypes.ResourceType;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test class for resource transaction requests.
 * 
 * Requests cannot be illegally constructed.
 * If this is attempted, an exception will be thrown.
 */
public class ResourceTransactionRequestTest {
    /*
    replacements:
        null -> fail
        empty -> depends on blanks
        wrong:
            different number:
                iBlanks
                oBlanks
            resource excluded:
                only excluded
                excluded (but zero) and not -> pass
        right:
            same number as iBlanks, whatever resource except exclusions
    containers:
        right:
        non-discardable output:
            same amount per each resource
        discardable output:
            lower or equal amount on any resource
        wrong:
        non-discardable output:
            lower or higher amount of any resource
        discardable output:
            higher amount of any resource
    */

    private final ResourceType rIn = new ResourceType("rIn", true);
    private final ResourceType rOut = new ResourceType("rOut", true);
    private final ResourceType rInRepl = new ResourceType("rInRepl", true);
    private final ResourceType rOutRepl = new ResourceType("rOutRepl", true);
    private final ResourceTransactionRecipe stdRecipe = new ResourceTransactionRecipe(Map.of(rIn, 1), 0, Set.of(), Map.of(rOut, 1), 0, Set.of(), false);
    private final Shelf cIn = new Shelf(2);
    private final Shelf cOut = new Shelf(2);

    @Test
    void nullInputReplacements() {
        assertThrows(NullPointerException.class, () ->
                new ResourceTransactionRequest(stdRecipe, Map.of(cIn, Map.of(rIn, 1)), null, Map.of(cOut, Map.of(rOut, 1)), Map.of()));
    }

    @Test
    void nullOutputReplacements() {
        assertThrows(NullPointerException.class, () ->
                new ResourceTransactionRequest(stdRecipe, Map.of(cIn, Map.of(rIn, 1)), Map.of(), Map.of(cOut, Map.of(rOut, 1)), null));
    }

    @Test
    void illegalInputReplacementsNumber() {
        assertThrows(IllegalArgumentException.class, () ->
                new ResourceTransactionRequest(stdRecipe, Map.of(cIn, Map.of(rIn, 1)), Map.of(rInRepl, -1), Map.of(cOut, Map.of(rOut, 1)), Map.of()));
        assertThrows(IllegalResourceTransactionReplacementsException.class, () ->
                new ResourceTransactionRequest(stdRecipe, Map.of(cIn, Map.of(rIn, 1)), Map.of(rInRepl, 2), Map.of(cOut, Map.of(rOut, 1)), Map.of()));
    }

    @Test
    void illegalOutputReplacementsNumber() {
        assertThrows(IllegalArgumentException.class, () ->
                new ResourceTransactionRequest(stdRecipe, Map.of(cIn, Map.of(rIn, 1)), Map.of(), Map.of(cOut, Map.of(rOut, 1)), Map.of(rOutRepl, -1)));
        assertThrows(IllegalResourceTransactionReplacementsException.class, () ->
                new ResourceTransactionRequest(stdRecipe, Map.of(cIn, Map.of(rIn, 1)), Map.of(), Map.of(cOut, Map.of(rOut, 1)), Map.of(rOutRepl, 2)));
    }

    @Test
    void illegalInputReplacementsResources() {
        ResourceTransactionRecipe exclusionsRecipe = new ResourceTransactionRecipe(Map.of(rIn, 1), 1, Set.of(rInRepl), Map.of(rOut, 1), 0, Set.of(), false);

        assertThrows(IllegalResourceTransactionReplacementsException.class, () ->
                new ResourceTransactionRequest(exclusionsRecipe, Map.of(cIn, Map.of(rIn, 1)), Map.of(rInRepl, 1), Map.of(cOut, Map.of(rOut, 1)), Map.of()));

        // TODO: Use non-storable resource as replacement instead
        assertDoesNotThrow(() -> new ResourceTransactionRequest(exclusionsRecipe, Map.of(cIn, Map.of(rIn, 2, rInRepl, 0)), Map.of(), Map.of(cOut, Map.of(rOut, 1)), Map.of()));
    }

    @Test
    void illegalOutputReplacementsResources() {
        ResourceTransactionRecipe exclusionsRecipe = new ResourceTransactionRecipe(Map.of(rIn, 1), 0, Set.of(), Map.of(rOut, 1), 1, Set.of(rOutRepl), false);

        assertThrows(IllegalResourceTransactionReplacementsException.class, () ->
                new ResourceTransactionRequest(exclusionsRecipe, Map.of(cIn, Map.of(rIn, 1)), Map.of(), Map.of(cOut, Map.of(rOut, 1)), Map.of(rOutRepl, 1)));

        // TODO: Use non-storable resource as replacement instead
        assertDoesNotThrow(() -> new ResourceTransactionRequest(exclusionsRecipe, Map.of(cIn, Map.of(rIn, 1)), Map.of(), Map.of(cOut, Map.of(rOut, 2, rOutRepl, 0)), Map.of()));
    }

    @Test
    void inputReplacements() {
        ResourceTransactionRecipe replacementsRecipe = new ResourceTransactionRecipe(Map.of(rIn, 1), 1, Set.of(), Map.of(rOut, 1), 0, Set.of(), false);

        // TODO: Use non-storable resource as replacement instead
        assertDoesNotThrow(() -> new ResourceTransactionRequest(replacementsRecipe, Map.of(cIn, Map.of(rIn, 1, rInRepl, 1)), Map.of(), Map.of(cOut, Map.of(rOut, 1)), Map.of()));
    }

    @Test
    void outputReplacements() {
        ResourceTransactionRecipe replacementsRecipe = new ResourceTransactionRecipe(Map.of(rIn, 1), 0, Set.of(), Map.of(rOut, 1), 1, Set.of(), false);

        // TODO: Use non-storable resource as replacement instead
        assertDoesNotThrow(() -> new ResourceTransactionRequest(replacementsRecipe, Map.of(cIn, Map.of(rIn, 1)), Map.of(), Map.of(cOut, Map.of(rOut, 1, rOutRepl, 1)), Map.of()));
    }

    @Test
    void nullInputContainers() {
        assertThrows(NullPointerException.class, () -> new ResourceTransactionRequest(stdRecipe, null, Map.of(), Map.of(cOut, Map.of(rOut, 1)), Map.of()));
    }

    @Test
    void nullOutputContainers() {
        assertThrows(NullPointerException.class, () -> new ResourceTransactionRequest(stdRecipe, Map.of(cIn, Map.of(rIn, 1)), Map.of(), null, Map.of()));
    }

    @Test
    void nonDiscardableOutInputContainerTest() {
        assertDoesNotThrow(() -> new ResourceTransactionRequest(stdRecipe, Map.of(cIn, Map.of(rIn, 1)), Map.of(), Map.of(cIn, Map.of(rOut, 1)), Map.of()));
        assertThrows(IllegalResourceTransactionContainersException.class, () -> new ResourceTransactionRequest(stdRecipe, Map.of(cIn, Map.of(rIn, 0)), Map.of(), Map.of(cOut, Map.of(rOut, 1)), Map.of()));
        assertThrows(IllegalResourceTransactionContainersException.class, () -> new ResourceTransactionRequest(stdRecipe, Map.of(cIn, Map.of(rIn, 2)), Map.of(), Map.of(cOut, Map.of(rOut, 1)), Map.of()));
    }

    @Test
    void nonDiscardableOutOutputContainerTest() {
        assertDoesNotThrow(() -> new ResourceTransactionRequest(stdRecipe, Map.of(cIn, Map.of(rIn, 1)), Map.of(), Map.of(cIn, Map.of(rOut, 1)), Map.of()));
        assertThrows(IllegalResourceTransactionContainersException.class, () -> new ResourceTransactionRequest(stdRecipe, Map.of(cIn, Map.of(rIn, 1)), Map.of(), Map.of(cOut, Map.of(rOut, 0)), Map.of()));
        assertThrows(IllegalResourceTransactionContainersException.class, () -> new ResourceTransactionRequest(stdRecipe, Map.of(cIn, Map.of(rIn, 1)), Map.of(), Map.of(cOut, Map.of(rOut, 2)), Map.of()));
    }

    @Test
    void discardableOutInputContainerTest() {
        ResourceTransactionRecipe discardableRecipe = new ResourceTransactionRecipe(Map.of(rIn, 1), 0, Set.of(), Map.of(rOut, 1), 0, Set.of(), true);

        assertDoesNotThrow(() -> new ResourceTransactionRequest(discardableRecipe, Map.of(cIn, Map.of(rIn, 1)), Map.of(), Map.of(cIn, Map.of(rOut, 1)), Map.of()));
        assertThrows(IllegalResourceTransactionContainersException.class, () -> new ResourceTransactionRequest(discardableRecipe, Map.of(cIn, Map.of(rIn, 0)), Map.of(), Map.of(cOut, Map.of(rOut, 1)), Map.of()));
        assertThrows(IllegalResourceTransactionContainersException.class, () -> new ResourceTransactionRequest(discardableRecipe, Map.of(cIn, Map.of(rIn, 2)), Map.of(), Map.of(cOut, Map.of(rOut, 1)), Map.of()));
    }

    @Test
    void discardableOutOutputContainerTest() {
        ResourceTransactionRecipe discardableRecipe = new ResourceTransactionRecipe(Map.of(rIn, 1), 0, Set.of(), Map.of(rOut, 1), 0, Set.of(), true);

        assertDoesNotThrow(() -> new ResourceTransactionRequest(discardableRecipe, Map.of(cIn, Map.of(rIn, 1)), Map.of(), Map.of(cOut, Map.of(rOut, 1)), Map.of()));
        assertDoesNotThrow(() -> new ResourceTransactionRequest(discardableRecipe, Map.of(cIn, Map.of(rIn, 1)), Map.of(), Map.of(cOut, Map.of(rOut, 0)), Map.of()));
        assertDoesNotThrow(() -> new ResourceTransactionRequest(discardableRecipe, Map.of(cIn, Map.of(rIn, 1)), Map.of(), Map.of(cOut, Map.of()), Map.of()));
        assertThrows(IllegalResourceTransactionContainersException.class, () -> new ResourceTransactionRequest(discardableRecipe, Map.of(cIn, Map.of(rIn, 1)), Map.of(), Map.of(cOut, Map.of(rOut, 2)), Map.of()));
    }

    @Test
    void discardableReplacedOutOutputContainerTest() {
        ResourceTransactionRecipe recipe = new ResourceTransactionRecipe(Map.of(rIn, 1), 0, Set.of(), Map.of(rOut, 1), 1, Set.of(), true);

        // TODO: Use non-storable resource as replacement instead

        assertDoesNotThrow(() -> new ResourceTransactionRequest(recipe, Map.of(cIn, Map.of(rIn, 1)), Map.of(), Map.of(cOut, Map.of(rOut, 1, rOutRepl, 1)), Map.of()));
        assertDoesNotThrow(() -> new ResourceTransactionRequest(recipe, Map.of(cIn, Map.of(rIn, 1)), Map.of(), Map.of(cOut, Map.of(rOut, 1)), Map.of()));
        assertDoesNotThrow(() -> new ResourceTransactionRequest(recipe, Map.of(cIn, Map.of(rIn, 1)), Map.of(), Map.of(cOut, Map.of(rOutRepl, 1)), Map.of()));
        assertDoesNotThrow(() -> new ResourceTransactionRequest(recipe, Map.of(cIn, Map.of(rIn, 1)), Map.of(), Map.of(cOut, Map.of(rOut, 0, rOutRepl, 0)), Map.of()));
        assertDoesNotThrow(() -> new ResourceTransactionRequest(recipe, Map.of(cIn, Map.of(rIn, 1)), Map.of(), Map.of(cOut, Map.of()), Map.of()));

        // TODO: Use non-storable resource as replacement instead
        // assertThrows(IllegalResourceTransactionContainersException.class, () -> new ResourceTransactionRequest(recipe, Map.of(cIn, Map.of(rIn, 1)), Map.of(), Map.of(cOut, Map.of(rOut, 2)), Map.of()));

        assertThrows(IllegalResourceTransactionContainersException.class, () -> new ResourceTransactionRequest(recipe, Map.of(cIn, Map.of(rIn, 1)), Map.of(), Map.of(cOut, Map.of(rOut, 2, rOutRepl, 1)), Map.of()));
        assertThrows(IllegalResourceTransactionContainersException.class, () -> new ResourceTransactionRequest(recipe, Map.of(cIn, Map.of(rIn, 1)), Map.of(), Map.of(cOut, Map.of(rOut, 1, rOutRepl, 2)), Map.of()));
    }
}
