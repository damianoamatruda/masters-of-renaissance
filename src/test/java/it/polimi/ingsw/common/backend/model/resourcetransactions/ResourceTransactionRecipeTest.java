package it.polimi.ingsw.common.backend.model.resourcetransactions;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import it.polimi.ingsw.common.backend.model.resourcetypes.ResourceType;

import java.util.*;

/**
 * Test class for ResourceTransactionRecipes.
 */
public class ResourceTransactionRecipeTest {
    private final ResourceType coin = new ResourceType("coin", true);

    @Test
    void nullInput() {
        assertThrows(NullPointerException.class, () -> new ResourceTransactionRecipe(null, 0, Set.of(coin), Map.of(coin, 1), 0, Set.of(coin), false));
    }

    @Test
    void emptyInput() {
        assertDoesNotThrow(() -> new ResourceTransactionRecipe(Map.of(), 0, Set.of(coin), Map.of(coin, 1), 0, Set.of(coin), false));
    }

    @Test
    void negativeInput() {
        assertThrows(IllegalArgumentException.class, () -> new ResourceTransactionRecipe(Map.of(coin, -1), 0, Set.of(coin), Map.of(coin, 1), 0, Set.of(coin), false));
    }

    @Test
    void nullOutput() {
        assertThrows(NullPointerException.class, () -> new ResourceTransactionRecipe(Map.of(coin, 1), 0, Set.of(coin), null, 0, Set.of(coin), false));
    }

    @Test
    void emptyOutput() {
        assertDoesNotThrow(() -> new ResourceTransactionRecipe(Map.of(coin, 1), 0, Set.of(coin), Map.of(), 0, Set.of(coin), false));
    }

    @Test
    void negativeOutput() {
        assertThrows(IllegalArgumentException.class, () -> new ResourceTransactionRecipe(Map.of(coin, 1), 0, Set.of(coin), Map.of(coin, -1), 0, Set.of(coin), false));
    }

    @Test
    void nullInputBlankExclusions() {
        assertThrows(NullPointerException.class, () -> new ResourceTransactionRecipe(Map.of(coin, 1), 0, null, Map.of(coin, 1), 0, Set.of(coin), false));
    }

    @Test
    void emptyInputBlankExclusions() {
        assertDoesNotThrow(() -> new ResourceTransactionRecipe(Map.of(coin, 1), 0, Set.of(), Map.of(coin, 1), 0, Set.of(coin), false));
    }

    @Test
    void nullOutputBlankExclusions() {
        assertThrows(NullPointerException.class, () -> new ResourceTransactionRecipe(Map.of(coin, 1), 0, Set.of(coin), Map.of(coin, 1), 0, null, false));
    }

    @Test
    void emptyOutputBlankExclusions() {
        assertDoesNotThrow(() -> new ResourceTransactionRecipe(Map.of(coin, 1), 0, Set.of(coin), Map.of(coin, 1), 0, Set.of(), false));
    }

    @Test
    void negativeInputReplacements() {
        assertThrows(IllegalArgumentException.class, () -> new ResourceTransactionRecipe(Map.of(coin, 1), -1, Set.of(coin), Map.of(coin, 1), 0, Set.of(coin), false));
    }

    @Test
    void negativeOutputReplacements() {
        assertThrows(IllegalArgumentException.class, () -> new ResourceTransactionRecipe(Map.of(coin, 1), 0, Set.of(coin), Map.of(coin, 1), -1, Set.of(coin), false));
    }

    @Test
    void legalInstantiation() {
        assertDoesNotThrow(() -> new ResourceTransactionRecipe(Map.of(coin, 1), 1, Set.of(coin), Map.of(coin, 1), 1, Set.of(coin), false));
        assertDoesNotThrow(() -> new ResourceTransactionRecipe(Map.of(coin, 1), 1, Set.of(coin), Map.of(coin, 1), 1, Set.of(coin), true));
    }
}
