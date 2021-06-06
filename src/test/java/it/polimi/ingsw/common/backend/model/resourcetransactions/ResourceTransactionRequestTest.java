package it.polimi.ingsw.common.backend.model.resourcetransactions;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import it.polimi.ingsw.common.backend.model.resourcecontainers.Shelf;
import it.polimi.ingsw.common.backend.model.resourcetypes.ResourceType;

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
        nondiscardable output:
            same amount per each resource
        discardable output:
            lower or equal amount on any resource
        wrong:
        nondiscardable output:
            lower or higher amount of any resource
        discardable output:
            higher amount of any resource
    */
    
    private ResourceType rIn = new ResourceType("rIn", true);
    private ResourceType rOut = new ResourceType("rOut", true);
    private ResourceType rInRepl = new ResourceType("rInRepl", true);
    private ResourceType rOutRepl = new ResourceType("rOutRepl", true);
    private ResourceTransactionRecipe stdRecipe = new ResourceTransactionRecipe(Map.of(rIn, 1), 0, Set.of(), Map.of(rOut, 1), 0, Set.of(), false);
    private Shelf cIn = new Shelf(2);
    private Shelf cOut = new Shelf(2);

    @Test
    void nullInputReplacements() {
        assertThrows(NullPointerException.class, () ->
            new ResourceTransactionRequest(stdRecipe, null, Map.of(), Map.of(cIn, Map.of(rIn, 1)), Map.of(cOut, Map.of(rOut, 1))));
    }

    @Test
    void nullOutputReplacements() {
        assertThrows(NullPointerException.class, () ->
            new ResourceTransactionRequest(stdRecipe, Map.of(), null, Map.of(cIn, Map.of(rIn, 1)), Map.of(cOut, Map.of(rOut, 1))));
    }

    @Test
    void illegalInputReplacementsNumber() {
        assertThrows(IllegalResourceTransactionReplacementsException.class, () ->
            new ResourceTransactionRequest(stdRecipe, Map.of(rInRepl, -1), Map.of(), Map.of(cIn, Map.of(rIn, 1)), Map.of(cOut, Map.of(rOut, 1))));
        assertThrows(IllegalResourceTransactionReplacementsException.class, () ->
            new ResourceTransactionRequest(stdRecipe, Map.of(rInRepl, 2), Map.of(), Map.of(cIn, Map.of(rIn, 1)), Map.of(cOut, Map.of(rOut, 1))));
    }

    @Test
    void illegalOutputReplacementsNumber() {
        assertThrows(IllegalResourceTransactionReplacementsException.class, () ->
            new ResourceTransactionRequest(stdRecipe, Map.of(), Map.of(rOutRepl, -1), Map.of(cIn, Map.of(rIn, 1)), Map.of(cOut, Map.of(rOut, 1))));
        assertThrows(IllegalResourceTransactionReplacementsException.class, () ->
            new ResourceTransactionRequest(stdRecipe, Map.of(), Map.of(rOutRepl, 2), Map.of(cIn, Map.of(rIn, 1)), Map.of(cOut, Map.of(rOut, 1))));
    }

    @Test
    void illegalInputReplacementsResources() {
        ResourceTransactionRecipe exclusionsRecipe = new ResourceTransactionRecipe(Map.of(rIn, 1), 1, Set.of(rInRepl), Map.of(rOut, 1), 0, Set.of(), false);

        assertThrows(IllegalResourceTransactionReplacementsException.class, () ->
            new ResourceTransactionRequest(exclusionsRecipe, Map.of(rInRepl, 1), Map.of(), Map.of(cIn, Map.of(rIn, 1)), Map.of(cOut, Map.of(rOut, 1))));
        assertDoesNotThrow(() -> new ResourceTransactionRequest(exclusionsRecipe, Map.of(rIn, 1, rInRepl, 0), Map.of(), Map.of(cIn, Map.of(rIn, 2)), Map.of(cOut, Map.of(rOut, 1))));
    }

    @Test
    void illegalOutputReplacementsResources() {
        ResourceTransactionRecipe exclusionsRecipe = new ResourceTransactionRecipe(Map.of(rIn, 1), 0, Set.of(), Map.of(rOut, 1), 1, Set.of(rOutRepl), false);

        assertThrows(IllegalResourceTransactionReplacementsException.class, () ->
            new ResourceTransactionRequest(exclusionsRecipe, Map.of(), Map.of(rOutRepl, 1), Map.of(cIn, Map.of(rIn, 1)), Map.of(cOut, Map.of(rOut, 1))));
        assertDoesNotThrow(() -> new ResourceTransactionRequest(exclusionsRecipe, Map.of(), Map.of(rOut, 1, rOutRepl, 0), Map.of(cIn, Map.of(rIn, 1)), Map.of(cOut, Map.of(rOut, 2))));
    }

    @Test
    void inputReplacements() {
        ResourceTransactionRecipe replacementsRecipe = new ResourceTransactionRecipe(Map.of(rIn, 1), 1, Set.of(), Map.of(rOut, 1), 0, Set.of(), false);

        assertDoesNotThrow(() -> new ResourceTransactionRequest(replacementsRecipe, Map.of(rInRepl, 1), Map.of(), Map.of(cIn, Map.of(rIn, 1, rInRepl, 1)), Map.of(cOut, Map.of(rOut, 1))));
    }

    @Test
    void outputReplacements() {
        ResourceTransactionRecipe replacementsRecipe = new ResourceTransactionRecipe(Map.of(rIn, 1), 0, Set.of(), Map.of(rOut, 1), 1, Set.of(), false);

        assertDoesNotThrow(() -> new ResourceTransactionRequest(replacementsRecipe, Map.of(), Map.of(rOutRepl, 1), Map.of(cIn, Map.of(rIn, 1)), Map.of(cOut, Map.of(rOut, 1, rOutRepl, 1))));
    }

    @Test
    void nullInputContainers() {
        assertThrows(NullPointerException.class, () -> new ResourceTransactionRequest(stdRecipe, Map.of(), Map.of(), null, Map.of(cOut, Map.of(rOut, 1))));
    }

    @Test
    void nullOutputContainers() {
        assertThrows(NullPointerException.class, () -> new ResourceTransactionRequest(stdRecipe, Map.of(), Map.of(), Map.of(cIn, Map.of(rIn, 1)), null));
    }

    @Test
    void nonDiscardableOutInputContainerTest() {
        assertDoesNotThrow(() -> new ResourceTransactionRequest(stdRecipe, Map.of(), Map.of(), Map.of(cIn, Map.of(rIn, 1)), Map.of(cIn, Map.of(rOut, 1))));
        assertThrows(IllegalResourceTransactionContainersException.class, () -> new ResourceTransactionRequest(stdRecipe, Map.of(), Map.of(), Map.of(cIn, Map.of(rIn, 0)), Map.of(cOut, Map.of(rOut, 1))));
        assertThrows(IllegalResourceTransactionContainersException.class, () -> new ResourceTransactionRequest(stdRecipe, Map.of(), Map.of(), Map.of(cIn, Map.of(rIn, 2)), Map.of(cOut, Map.of(rOut, 1))));
    }

    @Test
    void nonDiscardableOutOutputContainerTest() {
        assertDoesNotThrow(() -> new ResourceTransactionRequest(stdRecipe, Map.of(), Map.of(), Map.of(cIn, Map.of(rIn, 1)), Map.of(cIn, Map.of(rOut, 1))));
        assertThrows(IllegalResourceTransactionContainersException.class, () -> new ResourceTransactionRequest(stdRecipe, Map.of(), Map.of(), Map.of(cIn, Map.of(rIn, 1)), Map.of(cOut, Map.of(rOut, 0))));
        assertThrows(IllegalResourceTransactionContainersException.class, () -> new ResourceTransactionRequest(stdRecipe, Map.of(), Map.of(), Map.of(cIn, Map.of(rIn, 1)), Map.of(cOut, Map.of(rOut, 2))));
    }

    @Test
    void discardableOutInputContainerTest() {
        ResourceTransactionRecipe discardableRecipe = new ResourceTransactionRecipe(Map.of(rIn, 1), 0, Set.of(), Map.of(rOut, 1), 0, Set.of(), true);

        assertDoesNotThrow(() -> new ResourceTransactionRequest(discardableRecipe, Map.of(), Map.of(), Map.of(cIn, Map.of(rIn, 1)), Map.of(cIn, Map.of(rOut, 1))));
        assertThrows(IllegalResourceTransactionContainersException.class, () -> new ResourceTransactionRequest(discardableRecipe, Map.of(), Map.of(), Map.of(cIn, Map.of(rIn, 0)), Map.of(cOut, Map.of(rOut, 1))));
        assertThrows(IllegalResourceTransactionContainersException.class, () -> new ResourceTransactionRequest(discardableRecipe, Map.of(), Map.of(), Map.of(cIn, Map.of(rIn, 2)), Map.of(cOut, Map.of(rOut, 1))));
    }

    @Test
    void discardableOutOutputContainerTest() {
        ResourceTransactionRecipe discardableRecipe = new ResourceTransactionRecipe(Map.of(rIn, 1), 0, Set.of(), Map.of(rOut, 1), 0, Set.of(), true);

        assertDoesNotThrow(() -> new ResourceTransactionRequest(discardableRecipe, Map.of(), Map.of(), Map.of(cIn, Map.of(rIn, 1)), Map.of(cOut, Map.of(rOut, 1))));
        assertDoesNotThrow(() -> new ResourceTransactionRequest(discardableRecipe, Map.of(), Map.of(), Map.of(cIn, Map.of(rIn, 1)), Map.of(cOut, Map.of(rOut, 0))));
        assertDoesNotThrow(() -> new ResourceTransactionRequest(discardableRecipe, Map.of(), Map.of(), Map.of(cIn, Map.of(rIn, 1)), Map.of(cOut, Map.of())));
        assertThrows(IllegalResourceTransactionContainersException.class, () -> new ResourceTransactionRequest(discardableRecipe, Map.of(), Map.of(), Map.of(cIn, Map.of(rIn, 1)), Map.of(cOut, Map.of(rOut, 2))));
    }

    @Test
    void discardableReplacedOutOutputContainerTest() {
        ResourceTransactionRecipe recipe = new ResourceTransactionRecipe(Map.of(rIn, 1), 0, Set.of(), Map.of(rOut, 1), 1, Set.of(), true);

        assertDoesNotThrow(() -> new ResourceTransactionRequest(recipe, Map.of(), Map.of(rOutRepl, 1), Map.of(cIn, Map.of(rIn, 1)), Map.of(cOut, Map.of(rOut, 1, rOutRepl, 1))));
        assertDoesNotThrow(() -> new ResourceTransactionRequest(recipe, Map.of(), Map.of(rOutRepl, 1), Map.of(cIn, Map.of(rIn, 1)), Map.of(cOut, Map.of(rOut, 1))));
        assertDoesNotThrow(() -> new ResourceTransactionRequest(recipe, Map.of(), Map.of(rOutRepl, 1), Map.of(cIn, Map.of(rIn, 1)), Map.of(cOut, Map.of(rOutRepl, 1))));
        assertDoesNotThrow(() -> new ResourceTransactionRequest(recipe, Map.of(), Map.of(rOutRepl, 1), Map.of(cIn, Map.of(rIn, 1)), Map.of(cOut, Map.of(rOut, 0, rOutRepl, 0))));
        assertDoesNotThrow(() -> new ResourceTransactionRequest(recipe, Map.of(), Map.of(rOutRepl, 1), Map.of(cIn, Map.of(rIn, 1)), Map.of(cOut, Map.of())));
        assertThrows(IllegalResourceTransactionContainersException.class, () -> new ResourceTransactionRequest(recipe, Map.of(), Map.of(rOutRepl, 1), Map.of(cIn, Map.of(rIn, 1)), Map.of(cOut, Map.of(rOut, 2))));
        assertThrows(IllegalResourceTransactionContainersException.class, () -> new ResourceTransactionRequest(recipe, Map.of(), Map.of(rOutRepl, 1), Map.of(cIn, Map.of(rIn, 1)), Map.of(cOut, Map.of(rOut, 2, rOutRepl, 1))));
        assertThrows(IllegalResourceTransactionContainersException.class, () -> new ResourceTransactionRequest(recipe, Map.of(), Map.of(rOutRepl, 1), Map.of(cIn, Map.of(rIn, 1)), Map.of(cOut, Map.of(rOut, 1, rOutRepl, 2))));
    }
}
