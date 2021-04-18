package it.polimi.ingsw.server.model.resourcecontainers;

import it.polimi.ingsw.server.model.resourcetypes.ResourceType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Shelf.
 */
public class ShelfTest {
    @Test
    void resourceTypesOfNewShelf() {
        Shelf shelf = new Shelf(3);
        assertEquals(Set.of(), shelf.getResourceTypes());
    }

    @Test
    void quantityOfNewShelf() {
        Shelf shelf = new Shelf(3);
        assertEquals(0, shelf.getQuantity());
    }

    @Test
    void resourceQuantityOfNewShelf() {
        ResourceType r = new ResourceType("r", true);
        Shelf shelf = new Shelf(3);
        assertEquals(0, shelf.getResourceQuantity(r));
    }

    @Test
    void resourceTypeOfNewShelf() {
        Shelf shelf = new Shelf(3);
        assertTrue(shelf.getResourceType().isEmpty());
    }

    @Test
    void newShelfShouldBeEmpty() {
        Shelf shelf = new Shelf(3);
        assertTrue(shelf.isEmpty());
    }

    @Test
    void newShelfShouldNotBeFull() {
        Shelf shelf = new Shelf(3);
        assertFalse(shelf.isFull());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void resourceTypesOfShelf(int resourcesCount) throws IllegalResourceTransferException {
        ResourceType r = new ResourceType("r", true);
        Shelf shelf = new Shelf(5);
        for (int i = 0; i < resourcesCount; i++)
            shelf.addResource(r);
        assertEquals(Set.of(r), shelf.getResourceTypes());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void quantityOfShelf(int resourcesCount) throws IllegalResourceTransferException {
        ResourceType r = new ResourceType("r", true);
        Shelf shelf = new Shelf(5);
        for (int i = 0; i < resourcesCount; i++)
            shelf.addResource(r);
        assertEquals(resourcesCount, shelf.getQuantity());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void resourceQuantityOfShelf(int resourcesCount) throws IllegalResourceTransferException {
        ResourceType r = new ResourceType("r", true);
        Shelf shelf = new Shelf(5);
        for (int i = 0; i < resourcesCount; i++)
            shelf.addResource(r);
        assertEquals(resourcesCount, shelf.getResourceQuantity(r));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void shelfShouldNotBeEmpty(int resourcesCount) throws IllegalResourceTransferException {
        ResourceType r = new ResourceType("r", true);
        Shelf shelf = new Shelf(3);
        for (int i = 0; i < resourcesCount; i++)
            shelf.addResource(r);
        assertFalse(shelf.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void shelfShouldNotBeFull(int resourcesCount) throws IllegalResourceTransferException {
        ResourceType r = new ResourceType("r", true);
        Shelf shelf = new Shelf(resourcesCount + 1);
        for (int i = 0; i < resourcesCount; i++)
            shelf.addResource(r);
        assertFalse(shelf.isFull());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void shelfShouldBeFull(int resourcesCount) throws IllegalResourceTransferException {
        ResourceType r = new ResourceType("r", true);
        Shelf shelf = new Shelf(resourcesCount);
        for (int i = 0; i < resourcesCount; i++)
            shelf.addResource(r);
        assertTrue(shelf.isFull());
    }

    @Test
    void shelfShouldNotBeAbleToAddResourceOfAnotherType() throws IllegalResourceTransferException {
        ResourceType r1 = new ResourceType("r1", true);
        ResourceType r2 = new ResourceType("r2", true);
        Shelf shelf = new Shelf(5);
        for (int i = 0; i < 2; i++)
            shelf.addResource(r1);
        assertThrows(IllegalResourceTransferException.class, () -> shelf.addResource(r2));
    }

    @Test
    void shelfShouldNotBeAbleToRemoveResourceOfAnotherType() throws IllegalResourceTransferException {
        ResourceType r1 = new ResourceType("r1", true);
        ResourceType r2 = new ResourceType("r2", true);
        Shelf shelf = new Shelf(5);
        for (int i = 0; i < 3; i++)
            shelf.addResource(r1);
        assertThrows(IllegalResourceTransferException.class, () -> shelf.removeResource(r2));
    }

    @Test
    void emptyShelfShouldNotBeAbleToRemoveResource() {
        ResourceType r = new ResourceType("r", true);
        Shelf shelf = new Shelf(5);
        assertThrows(IllegalResourceTransferException.class, () -> shelf.removeResource(r));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void quantityOfShelfWithOneRemovedResource(int resourcesCount) throws IllegalResourceTransferException {
        ResourceType r = new ResourceType("r", true);
        Shelf shelf = new Shelf(5);
        for (int i = 0; i < resourcesCount; i++)
            shelf.addResource(r);
        shelf.removeResource(r);
        assertEquals(resourcesCount - 1, shelf.getQuantity());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void resourceQuantityOfShelfWithOneRemovedResource(int resourcesCount) throws IllegalResourceTransferException {
        ResourceType r = new ResourceType("r", true);
        Shelf shelf = new Shelf(5);
        for (int i = 0; i < resourcesCount; i++)
            shelf.addResource(r);
        shelf.removeResource(r);
        assertEquals(resourcesCount - 1, shelf.getResourceQuantity(r));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void resourceTypesOfClearedShelf(int resourcesCount) throws IllegalResourceTransferException {
        ResourceType r = new ResourceType("r", true);
        Shelf shelf = new Shelf(3);
        for (int i = 0; i < resourcesCount; i++)
            shelf.addResource(r);
        for (int i = 0; i < resourcesCount; i++)
            shelf.removeResource(r);
        assertEquals(Set.of(), shelf.getResourceTypes());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void quantityOfClearedShelf(int resourcesCount) throws IllegalResourceTransferException {
        ResourceType r = new ResourceType("r", true);
        Shelf shelf = new Shelf(3);
        for (int i = 0; i < resourcesCount; i++)
            shelf.addResource(r);
        for (int i = 0; i < resourcesCount; i++)
            shelf.removeResource(r);
        assertEquals(0, shelf.getQuantity());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void resourceQuantityOfClearedShelf(int resourcesCount) throws IllegalResourceTransferException {
        ResourceType r = new ResourceType("r", true);
        Shelf shelf = new Shelf(3);
        for (int i = 0; i < resourcesCount; i++)
            shelf.addResource(r);
        for (int i = 0; i < resourcesCount; i++)
            shelf.removeResource(r);
        assertEquals(0, shelf.getResourceQuantity(r));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void resourceTypeOfClearedShelf(int resourcesCount) throws IllegalResourceTransferException {
        ResourceType r = new ResourceType("r", true);
        Shelf shelf = new Shelf(3);
        for (int i = 0; i < resourcesCount; i++)
            shelf.addResource(r);
        for (int i = 0; i < resourcesCount; i++)
            shelf.removeResource(r);
        assertTrue(shelf.getResourceType().isEmpty());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void clearedShelfShouldBeEmpty(int resourcesCount) throws IllegalResourceTransferException {
        ResourceType r = new ResourceType("r", true);
        Shelf shelf = new Shelf(3);
        for (int i = 0; i < resourcesCount; i++)
            shelf.addResource(r);
        for (int i = 0; i < resourcesCount; i++)
            shelf.removeResource(r);
        assertTrue(shelf.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void clearedShelfShouldNotBeFull(int resourcesCount) throws IllegalResourceTransferException {
        ResourceType r = new ResourceType("r", true);
        Shelf shelf = new Shelf(3);
        for (int i = 0; i < resourcesCount; i++)
            shelf.addResource(r);
        for (int i = 0; i < resourcesCount; i++)
            shelf.removeResource(r);
        assertFalse(shelf.isFull());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void clearedShelfShouldNotBeAbleToRemoveResources(int resourcesCount) throws IllegalResourceTransferException {
        ResourceType r = new ResourceType("r", true);
        Shelf shelf = new Shelf(5);
        for (int i = 0; i < resourcesCount; i++)
            shelf.addResource(r);
        for (int i = 0; i < resourcesCount; i++)
            shelf.removeResource(r);
        assertThrows(IllegalResourceTransferException.class, () -> shelf.removeResource(r));
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void swapShouldBePossible(boolean direct) throws IllegalResourceTransferException {
        ResourceType r1 = new ResourceType("r1", true);
        ResourceType r2 = new ResourceType("r2", true);

        Shelf shelf1 = new Shelf(7);
        for (int i = 0; i < 3; i++)
            shelf1.addResource(r1);

        Shelf shelf2 = new Shelf(13);
        for (int i = 0; i < 5; i++)
            shelf2.addResource(r2);

        if (direct)
            Shelf.swap(shelf1, shelf2);
        else
            Shelf.swap(shelf2, shelf1);

        assertAll("shelves",
                () -> assertAll("shelf1",
                        () -> assertEquals(7, shelf1.getSize()),
                        () -> assertEquals(Set.of(r2), shelf1.getResourceTypes()),
                        () -> assertEquals(5, shelf1.getQuantity()),
                        () -> assertEquals(5, shelf1.getResourceQuantity(r2))
                ),
                () -> assertAll("shelf2",
                        () -> assertEquals(13, shelf2.getSize()),
                        () -> assertEquals(Set.of(r1), shelf2.getResourceTypes()),
                        () -> assertEquals(3, shelf2.getQuantity()),
                        () -> assertEquals(3, shelf2.getResourceQuantity(r1))
                )
        );
    }

    @Test
    void swapShouldNotBePossibleBecauseOfQuantities() throws IllegalResourceTransferException {
        ResourceType r = new ResourceType("r", true);

        Shelf shelf1 = new Shelf(3);
        for (int i = 0; i < 3; i++)
            shelf1.addResource(r);

        Shelf shelf2 = new Shelf(5);
        for (int i = 0; i < 5; i++)
            shelf2.addResource(r);

        assertThrows(IllegalResourceTransferException.class, () -> Shelf.swap(shelf1, shelf2));
    }

    @Test
    void swapTwice() throws IllegalResourceTransferException {
        ResourceType r1 = new ResourceType("r1", true);
        ResourceType r2 = new ResourceType("r2", true);

        Shelf shelf1 = new Shelf(7);
        for (int i = 0; i < 3; i++)
            shelf1.addResource(r1);

        Shelf shelf2 = new Shelf(13);
        for (int i = 0; i < 5; i++)
            shelf2.addResource(r2);

        Shelf.swap(shelf1, shelf2);
        Shelf.swap(shelf1, shelf2);

        assertAll("shelves",
                () -> assertAll("shelf1",
                        () -> assertEquals(7, shelf1.getSize()),
                        () -> assertEquals(Set.of(r1), shelf1.getResourceTypes()),
                        () -> assertEquals(3, shelf1.getQuantity()),
                        () -> assertEquals(3, shelf1.getResourceQuantity(r1))
                ),
                () -> assertAll("shelf2",
                        () -> assertEquals(13, shelf2.getSize()),
                        () -> assertEquals(Set.of(r2), shelf2.getResourceTypes()),
                        () -> assertEquals(5, shelf2.getQuantity()),
                        () -> assertEquals(5, shelf2.getResourceQuantity(r2))
                )
        );
    }
}
