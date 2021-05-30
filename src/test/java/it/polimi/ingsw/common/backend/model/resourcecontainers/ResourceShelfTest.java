package it.polimi.ingsw.common.backend.model.resourcecontainers;

import it.polimi.ingsw.common.backend.model.resourcetypes.ResourceType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Map;
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
        resourceShelf.addResources(Map.of(r, resourcesCount));
        assertEquals(Set.of(r), resourceShelf.getResourceTypes());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void quantityOfResourceShelf(int resourcesCount) throws IllegalResourceTransferException {
        ResourceType r = new ResourceType("r", true);
        ResourceShelf resourceShelf = new ResourceShelf(r, 5);
        resourceShelf.addResources(Map.of(r, resourcesCount));
        assertEquals(resourcesCount, resourceShelf.getQuantity());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void resourceQuantityOfResourceShelf(int resourcesCount) throws IllegalResourceTransferException {
        ResourceType r = new ResourceType("r", true);
        ResourceShelf resourceShelf = new ResourceShelf(r, 5);
        resourceShelf.addResources(Map.of(r, resourcesCount));
        assertEquals(resourcesCount, resourceShelf.getResourceQuantity(r));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void resourceShelfShouldNotBeEmpty(int resourcesCount) throws IllegalResourceTransferException {
        ResourceType r = new ResourceType("r", true);
        ResourceShelf resourceShelf = new ResourceShelf(r, 3);
        resourceShelf.addResources(Map.of(r, resourcesCount));
        assertFalse(resourceShelf.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void resourceShelfShouldNotBeFull(int resourcesCount) throws IllegalResourceTransferException {
        ResourceType r = new ResourceType("r", true);
        ResourceShelf resourceShelf = new ResourceShelf(r, resourcesCount + 1);
        resourceShelf.addResources(Map.of(r, resourcesCount));
        assertFalse(resourceShelf.isFull());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void resourceShelfShouldBeFull(int resourcesCount) throws IllegalResourceTransferException {
        ResourceType r = new ResourceType("r", true);
        ResourceShelf resourceShelf = new ResourceShelf(r, resourcesCount);
        resourceShelf.addResources(Map.of(r, resourcesCount));
        assertTrue(resourceShelf.isFull());
    }

    @Test
    void resourceShelfShouldNotBeAbleToAddResourceOfAnotherType() throws IllegalResourceTransferException {
        ResourceType r1 = new ResourceType("r1", true);
        ResourceType r2 = new ResourceType("r2", true);
        ResourceShelf resourceShelf = new ResourceShelf(r1, 5);
        resourceShelf.addResources(Map.of(r1, 2));
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
        resourceShelf.addResources(Map.of(r1, 3));
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
        resourceShelf.addResources(Map.of(r, resourcesCount));
        resourceShelf.removeResource(r);
        assertEquals(resourcesCount - 1, resourceShelf.getQuantity());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void resourceQuantityOfResourceShelfWithOneRemovedResource(int resourcesCount) throws IllegalResourceTransferException {
        ResourceType r = new ResourceType("r", true);
        ResourceShelf resourceShelf = new ResourceShelf(r, 5);
        resourceShelf.addResources(Map.of(r, resourcesCount));
        resourceShelf.removeResource(r);
        assertEquals(resourcesCount - 1, resourceShelf.getResourceQuantity(r));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void resourceTypesOfClearedShelf(int resourcesCount) throws IllegalResourceTransferException {
        ResourceType r = new ResourceType("r", true);
        ResourceShelf resourceShelf = new ResourceShelf(r, 3);
        resourceShelf.addResources(Map.of(r, resourcesCount));
        resourceShelf.removeResources(Map.of(r, resourcesCount));
        assertEquals(Set.of(), resourceShelf.getResourceTypes());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void quantityOfClearedShelf(int resourcesCount) throws IllegalResourceTransferException {
        ResourceType r = new ResourceType("r", true);
        ResourceShelf resourceShelf = new ResourceShelf(r, 3);
        resourceShelf.addResources(Map.of(r, resourcesCount));
        resourceShelf.removeResources(Map.of(r, resourcesCount));
        assertEquals(0, resourceShelf.getQuantity());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void resourceQuantityOfClearedShelf(int resourcesCount) throws IllegalResourceTransferException {
        ResourceType r = new ResourceType("r", true);
        ResourceShelf resourceShelf = new ResourceShelf(r, 3);
        resourceShelf.addResources(Map.of(r, resourcesCount));
        resourceShelf.removeResources(Map.of(r, resourcesCount));
        assertEquals(0, resourceShelf.getResourceQuantity(r));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void resourceTypeOfClearedShelf(int resourcesCount) throws IllegalResourceTransferException {
        ResourceType r = new ResourceType("r", true);
        ResourceShelf resourceShelf = new ResourceShelf(r, 3);
        resourceShelf.addResources(Map.of(r, resourcesCount));
        resourceShelf.removeResources(Map.of(r, resourcesCount));
        assertTrue(resourceShelf.getResourceType().isEmpty());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void clearedResourceShelfShouldBeEmpty(int resourcesCount) throws IllegalResourceTransferException {
        ResourceType r = new ResourceType("r", true);
        ResourceShelf resourceShelf = new ResourceShelf(r, 3);
        resourceShelf.addResources(Map.of(r, resourcesCount));
        resourceShelf.removeResources(Map.of(r, resourcesCount));
        assertTrue(resourceShelf.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void clearedResourceShelfShouldNotBeFull(int resourcesCount) throws IllegalResourceTransferException {
        ResourceType r = new ResourceType("r", true);
        ResourceShelf resourceShelf = new ResourceShelf(r, 3);
        resourceShelf.addResources(Map.of(r, resourcesCount));
        resourceShelf.removeResources(Map.of(r, resourcesCount));
        assertFalse(resourceShelf.isFull());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void clearedResourceShelfShouldNotBeAbleToRemoveResources(int resourcesCount) throws IllegalResourceTransferException {
        ResourceType r = new ResourceType("r", true);
        ResourceShelf resourceShelf = new ResourceShelf(r, 5);
        resourceShelf.addResources(Map.of(r, resourcesCount));
        resourceShelf.removeResources(Map.of(r, resourcesCount));
        assertThrows(IllegalResourceTransferException.class, () -> resourceShelf.removeResource(r));
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void swapShouldBePossible(boolean direct) throws IllegalResourceTransferException {
        ResourceType r = new ResourceType("r", true);

        ResourceShelf resourceShelf1 = new ResourceShelf(r, 7);
        resourceShelf1.addResources(Map.of(r, 3));

        ResourceShelf resourceShelf2 = new ResourceShelf(r, 13);
        resourceShelf2.addResources(Map.of(r, 5));

        if (direct)
            ResourceContainer.swap(resourceShelf1, resourceShelf2);
        else
            ResourceContainer.swap(resourceShelf2, resourceShelf1);

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
        resourceShelf1.addResources(Map.of(r, 3));

        ResourceShelf resourceShelf2 = new ResourceShelf(r, 5);
        resourceShelf2.addResources(Map.of(r, 5));

        assertThrows(IllegalResourceTransferException.class, () -> ResourceContainer.swap(resourceShelf1, resourceShelf2));
    }

    @Test
    void swapShouldNotBePossibleBecauseOfResourceTypes() throws IllegalResourceTransferException {
        ResourceType r1 = new ResourceType("r1", true);
        ResourceType r2 = new ResourceType("r2", true);

        ResourceShelf resourceShelf1 = new ResourceShelf(r1, 7);
        resourceShelf1.addResources(Map.of(r1, 3));

        ResourceShelf resourceShelf2 = new ResourceShelf(r2, 13);
        resourceShelf2.addResources(Map.of(r2, 5));

        assertThrows(IllegalResourceTransferException.class, () -> ResourceContainer.swap(resourceShelf1, resourceShelf2));
    }

    @Test
    void swapTwice() throws IllegalResourceTransferException {
        ResourceType r = new ResourceType("r", true);

        ResourceShelf resourceShelf1 = new ResourceShelf(r, 7);
        resourceShelf1.addResources(Map.of(r, 3));

        ResourceShelf resourceShelf2 = new ResourceShelf(r, 13);
        resourceShelf2.addResources(Map.of(r, 5));

        ResourceContainer.swap(resourceShelf1, resourceShelf2);
        ResourceContainer.swap(resourceShelf1, resourceShelf2);

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
