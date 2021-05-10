package it.polimi.ingsw.common.backend.model.resourcecontainers;

import it.polimi.ingsw.common.backend.model.resourcetypes.ResourceType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for ResourceShelf.
 */
public class ResourceShelfTest {
    @Test
    void resourceTypesOfNewResourceShelf() {
        ResourceType r = new ResourceType("r", true);
        ResourceShelf resourceShelf = new ResourceShelf(r, 3);
        assertEquals(Set.of(), resourceShelf.getResourceTypes());
    }

    @Test
    void quantityOfNewShelf() {
        ResourceType r = new ResourceType("r", true);
        ResourceShelf resourceShelf = new ResourceShelf(r, 3);
        assertEquals(0, resourceShelf.getQuantity());
    }

    @Test
    void resourceQuantityOfNewShelf() {
        ResourceType r = new ResourceType("r", true);
        ResourceShelf resourceShelf = new ResourceShelf(r, 3);
        assertEquals(0, resourceShelf.getResourceQuantity(r));
    }

    @Test
    void resourceTypeOfNewShelf() {
        ResourceType r = new ResourceType("r", true);
        ResourceShelf resourceShelf = new ResourceShelf(r, 3);
        assertTrue(resourceShelf.getResourceType().isEmpty());
    }

    @Test
    void newShelfShouldBeEmpty() {
        ResourceType r = new ResourceType("r", true);
        ResourceShelf resourceShelf = new ResourceShelf(r, 3);
        assertTrue(resourceShelf.isEmpty());
    }

    @Test
    void newShelfShouldNotBeFull() {
        ResourceType r = new ResourceType("r", true);
        ResourceShelf resourceShelf = new ResourceShelf(r, 3);
        assertFalse(resourceShelf.isFull());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void resourceTypesOfResourceShelf(int resourcesCount) throws IllegalResourceTransferException {
        ResourceType r = new ResourceType("r", true);
        ResourceShelf resourceShelf = new ResourceShelf(r, 5);
        for (int i = 0; i < resourcesCount; i++)
            resourceShelf.addResource(r);
        assertEquals(Set.of(r), resourceShelf.getResourceTypes());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void quantityOfResourceShelf(int resourcesCount) throws IllegalResourceTransferException {
        ResourceType r = new ResourceType("r", true);
        ResourceShelf resourceShelf = new ResourceShelf(r, 5);
        for (int i = 0; i < resourcesCount; i++)
            resourceShelf.addResource(r);
        assertEquals(resourcesCount, resourceShelf.getQuantity());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void resourceQuantityOfResourceShelf(int resourcesCount) throws IllegalResourceTransferException {
        ResourceType r = new ResourceType("r", true);
        ResourceShelf resourceShelf = new ResourceShelf(r, 5);
        for (int i = 0; i < resourcesCount; i++)
            resourceShelf.addResource(r);
        assertEquals(resourcesCount, resourceShelf.getResourceQuantity(r));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void resourceShelfShouldNotBeEmpty(int resourcesCount) throws IllegalResourceTransferException {
        ResourceType r = new ResourceType("r", true);
        ResourceShelf resourceShelf = new ResourceShelf(r, 3);
        for (int i = 0; i < resourcesCount; i++)
            resourceShelf.addResource(r);
        assertFalse(resourceShelf.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void resourceShelfShouldNotBeFull(int resourcesCount) throws IllegalResourceTransferException {
        ResourceType r = new ResourceType("r", true);
        ResourceShelf resourceShelf = new ResourceShelf(r, resourcesCount + 1);
        for (int i = 0; i < resourcesCount; i++)
            resourceShelf.addResource(r);
        assertFalse(resourceShelf.isFull());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void resourceShelfShouldBeFull(int resourcesCount) throws IllegalResourceTransferException {
        ResourceType r = new ResourceType("r", true);
        ResourceShelf resourceShelf = new ResourceShelf(r, resourcesCount);
        for (int i = 0; i < resourcesCount; i++)
            resourceShelf.addResource(r);
        assertTrue(resourceShelf.isFull());
    }

    @Test
    void resourceShelfShouldNotBeAbleToAddResourceOfAnotherType() throws IllegalResourceTransferException {
        ResourceType r1 = new ResourceType("r1", true);
        ResourceType r2 = new ResourceType("r2", true);
        ResourceShelf resourceShelf = new ResourceShelf(r1, 5);
        for (int i = 0; i < 2; i++)
            resourceShelf.addResource(r1);
        assertThrows(IllegalResourceTransferException.class, () -> resourceShelf.addResource(r2));
    }

    @Test
    void emptyResourceShelfShouldNotBeAbleToAddResourceOfNonBoundedType() {
        ResourceType r1 = new ResourceType("r1", true);
        ResourceType r2 = new ResourceType("r2", true);
        ResourceShelf resourceShelf = new ResourceShelf(r1, 5);
        assertThrows(IllegalResourceTransferException.class, () -> resourceShelf.addResource(r2));
    }

    @Test
    void resourceShelfShouldNotBeAbleToRemoveResourceOfAnotherType() throws IllegalResourceTransferException {
        ResourceType r1 = new ResourceType("r1", true);
        ResourceType r2 = new ResourceType("r2", true);
        ResourceShelf resourceShelf = new ResourceShelf(r1, 5);
        for (int i = 0; i < 3; i++)
            resourceShelf.addResource(r1);
        assertThrows(IllegalResourceTransferException.class, () -> resourceShelf.removeResource(r2));
    }

    @Test
    void emptyResourceShelfShouldNotBeAbleToRemoveResource() {
        ResourceType r = new ResourceType("r", true);
        ResourceShelf resourceShelf = new ResourceShelf(r, 5);
        assertThrows(IllegalResourceTransferException.class, () -> resourceShelf.removeResource(r));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void quantityOfResourceShelfWithOneRemovedResource(int resourcesCount) throws IllegalResourceTransferException {
        ResourceType r = new ResourceType("r", true);
        ResourceShelf resourceShelf = new ResourceShelf(r, 5);
        for (int i = 0; i < resourcesCount; i++)
            resourceShelf.addResource(r);
        resourceShelf.removeResource(r);
        assertEquals(resourcesCount - 1, resourceShelf.getQuantity());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void resourceQuantityOfResourceShelfWithOneRemovedResource(int resourcesCount) throws IllegalResourceTransferException {
        ResourceType r = new ResourceType("r", true);
        ResourceShelf resourceShelf = new ResourceShelf(r, 5);
        for (int i = 0; i < resourcesCount; i++)
            resourceShelf.addResource(r);
        resourceShelf.removeResource(r);
        assertEquals(resourcesCount - 1, resourceShelf.getResourceQuantity(r));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void resourceTypesOfClearedShelf(int resourcesCount) throws IllegalResourceTransferException {
        ResourceType r = new ResourceType("r", true);
        ResourceShelf resourceShelf = new ResourceShelf(r, 3);
        for (int i = 0; i < resourcesCount; i++)
            resourceShelf.addResource(r);
        for (int i = 0; i < resourcesCount; i++)
            resourceShelf.removeResource(r);
        assertEquals(Set.of(), resourceShelf.getResourceTypes());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void quantityOfClearedShelf(int resourcesCount) throws IllegalResourceTransferException {
        ResourceType r = new ResourceType("r", true);
        ResourceShelf resourceShelf = new ResourceShelf(r, 3);
        for (int i = 0; i < resourcesCount; i++)
            resourceShelf.addResource(r);
        for (int i = 0; i < resourcesCount; i++)
            resourceShelf.removeResource(r);
        assertEquals(0, resourceShelf.getQuantity());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void resourceQuantityOfClearedShelf(int resourcesCount) throws IllegalResourceTransferException {
        ResourceType r = new ResourceType("r", true);
        ResourceShelf resourceShelf = new ResourceShelf(r, 3);
        for (int i = 0; i < resourcesCount; i++)
            resourceShelf.addResource(r);
        for (int i = 0; i < resourcesCount; i++)
            resourceShelf.removeResource(r);
        assertEquals(0, resourceShelf.getResourceQuantity(r));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void resourceTypeOfClearedShelf(int resourcesCount) throws IllegalResourceTransferException {
        ResourceType r = new ResourceType("r", true);
        ResourceShelf resourceShelf = new ResourceShelf(r, 3);
        for (int i = 0; i < resourcesCount; i++)
            resourceShelf.addResource(r);
        for (int i = 0; i < resourcesCount; i++)
            resourceShelf.removeResource(r);
        assertTrue(resourceShelf.getResourceType().isEmpty());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void clearedResourceShelfShouldBeEmpty(int resourcesCount) throws IllegalResourceTransferException {
        ResourceType r = new ResourceType("r", true);
        ResourceShelf resourceShelf = new ResourceShelf(r, 3);
        for (int i = 0; i < resourcesCount; i++)
            resourceShelf.addResource(r);
        for (int i = 0; i < resourcesCount; i++)
            resourceShelf.removeResource(r);
        assertTrue(resourceShelf.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void clearedResourceShelfShouldNotBeFull(int resourcesCount) throws IllegalResourceTransferException {
        ResourceType r = new ResourceType("r", true);
        ResourceShelf resourceShelf = new ResourceShelf(r, 3);
        for (int i = 0; i < resourcesCount; i++)
            resourceShelf.addResource(r);
        for (int i = 0; i < resourcesCount; i++)
            resourceShelf.removeResource(r);
        assertFalse(resourceShelf.isFull());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void clearedResourceShelfShouldNotBeAbleToRemoveResources(int resourcesCount) throws IllegalResourceTransferException {
        ResourceType r = new ResourceType("r", true);
        ResourceShelf resourceShelf = new ResourceShelf(r, 5);
        for (int i = 0; i < resourcesCount; i++)
            resourceShelf.addResource(r);
        for (int i = 0; i < resourcesCount; i++)
            resourceShelf.removeResource(r);
        assertThrows(IllegalResourceTransferException.class, () -> resourceShelf.removeResource(r));
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void swapShouldBePossible(boolean direct) throws IllegalResourceTransferException {
        ResourceType r = new ResourceType("r", true);

        ResourceShelf resourceShelf1 = new ResourceShelf(r, 7);
        for (int i = 0; i < 3; i++)
            resourceShelf1.addResource(r);

        ResourceShelf resourceShelf2 = new ResourceShelf(r, 13);
        for (int i = 0; i < 5; i++)
            resourceShelf2.addResource(r);

        if (direct)
            Shelf.swap(resourceShelf1, resourceShelf2);
        else
            Shelf.swap(resourceShelf2, resourceShelf1);

        assertAll("shelves",
                () -> assertAll("resourceShelf1",
                        () -> assertEquals(7, resourceShelf1.getSize()),
                        () -> assertEquals(Set.of(r), resourceShelf1.getResourceTypes()),
                        () -> assertEquals(5, resourceShelf1.getQuantity()),
                        () -> assertEquals(5, resourceShelf1.getResourceQuantity(r))
                ),
                () -> assertAll("resourceShelf2",
                        () -> assertEquals(13, resourceShelf2.getSize()),
                        () -> assertEquals(Set.of(r), resourceShelf2.getResourceTypes()),
                        () -> assertEquals(3, resourceShelf2.getQuantity()),
                        () -> assertEquals(3, resourceShelf2.getResourceQuantity(r))
                )
        );
    }

    @Test
    void swapShouldNotBePossibleBecauseOfQuantities() throws IllegalResourceTransferException {
        ResourceType r = new ResourceType("r", true);

        ResourceShelf resourceShelf1 = new ResourceShelf(r, 3);
        for (int i = 0; i < 3; i++)
            resourceShelf1.addResource(r);

        ResourceShelf resourceShelf2 = new ResourceShelf(r, 5);
        for (int i = 0; i < 5; i++)
            resourceShelf2.addResource(r);

        assertThrows(IllegalResourceTransferException.class, () -> Shelf.swap(resourceShelf1, resourceShelf2));
    }

    @Test
    void swapShouldNotBePossibleBecauseOfResourceTypes() throws IllegalResourceTransferException {
        ResourceType r1 = new ResourceType("r1", true);
        ResourceType r2 = new ResourceType("r2", true);

        ResourceShelf resourceShelf1 = new ResourceShelf(r1, 7);
        for (int i = 0; i < 3; i++)
            resourceShelf1.addResource(r1);

        ResourceShelf resourceShelf2 = new ResourceShelf(r2, 13);
        for (int i = 0; i < 5; i++)
            resourceShelf2.addResource(r2);

        assertThrows(IllegalResourceTransferException.class, () -> Shelf.swap(resourceShelf1, resourceShelf2));
    }

    @Test
    void swapTwice() throws IllegalResourceTransferException {
        ResourceType r = new ResourceType("r", true);

        ResourceShelf resourceShelf1 = new ResourceShelf(r, 7);
        for (int i = 0; i < 3; i++)
            resourceShelf1.addResource(r);

        ResourceShelf resourceShelf2 = new ResourceShelf(r, 13);
        for (int i = 0; i < 5; i++)
            resourceShelf2.addResource(r);

        Shelf.swap(resourceShelf1, resourceShelf2);
        Shelf.swap(resourceShelf1, resourceShelf2);

        assertAll("shelves",
                () -> assertAll("resourceShelf1",
                        () -> assertEquals(7, resourceShelf1.getSize()),
                        () -> assertEquals(Set.of(r), resourceShelf1.getResourceTypes()),
                        () -> assertEquals(3, resourceShelf1.getQuantity()),
                        () -> assertEquals(3, resourceShelf1.getResourceQuantity(r))
                ),
                () -> assertAll("resourceShelf2",
                        () -> assertEquals(13, resourceShelf2.getSize()),
                        () -> assertEquals(Set.of(r), resourceShelf2.getResourceTypes()),
                        () -> assertEquals(5, resourceShelf2.getQuantity()),
                        () -> assertEquals(5, resourceShelf2.getResourceQuantity(r))
                )
        );
    }
}
