package it.polimi.ingsw.model.resourcecontainers;

import it.polimi.ingsw.model.resourcetypes.ResourceType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Strongbox.
 */
class StrongboxTest {
    @Test
    void newStrongboxShouldBeEmpty() {
        Strongbox strongbox = new Strongbox();
        assertTrue(strongbox.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(ints = { 1, 2, 3 })
    void resourceTypesOfShelfWithResourcesOfSameType(int resourcesCount) {
        Strongbox strongbox = new Strongbox();
        ResourceType r = new ResourceType("r", true);
        for (int i = 0; i < resourcesCount; i++)
            strongbox.addResource(r);
        assertEquals(Set.of(r), strongbox.getResourceTypes());
    }
    
    @ParameterizedTest
    @ValueSource(ints = { 1, 2, 3 })
    void quantityOfStrongboxWithResourcesOfSameType(int resourcesCount) {
        Strongbox strongbox = new Strongbox();
        ResourceType r = new ResourceType("r", true);
        for (int i = 0; i < resourcesCount; i++)
            strongbox.addResource(r);
        assertEquals(resourcesCount, strongbox.getQuantity());
    }

    @ParameterizedTest
    @ValueSource(ints = { 1, 2, 3 })
    void resourceQuantityOfStrongboxWithResourcesOfSameType(int resourcesCount) {
        Strongbox strongbox = new Strongbox();
        ResourceType r = new ResourceType("r", true);
        for (int i = 0; i < resourcesCount; i++)
            strongbox.addResource(r);
        assertEquals(resourcesCount, strongbox.getResourceQuantity(r));
    }

    @ParameterizedTest
    @ValueSource(ints = { 1, 2, 3 })
    void strongboxWithResourcesOfSameTypeShouldNotBeEmpty(int resourcesCount) {
        Strongbox strongbox = new Strongbox();
        ResourceType r = new ResourceType("r", true);
        for (int i = 0; i < resourcesCount; i++)
            strongbox.addResource(r);
        assertFalse(strongbox.isEmpty());
    }

    @Test
    void resourceTypesOfStrongboxWithResourcesOfMultipleTypes() throws IllegalResourceTransferException {
        Strongbox strongbox = new Strongbox();
        ResourceType r1 = new ResourceType("r1", true);
        ResourceType r2 = new ResourceType("r2", true);
        ResourceType r3 = new ResourceType("r3", true);
        for (int i = 0; i < 2; i++)
            strongbox.addResource(r1);
        for (int i = 0; i < 3; i++)
            strongbox.addResource(r2);
        for (int i = 0; i < 5; i++)
            strongbox.addResource(r3);
        assertEquals(Set.of(r1, r2, r3), strongbox.getResourceTypes());
    }

    @Test
    void quantityOfStrongboxWithResourcesOfMultipleTypes() throws IllegalResourceTransferException {
        Strongbox strongbox = new Strongbox();
        ResourceType r1 = new ResourceType("r1", true);
        ResourceType r2 = new ResourceType("r2", true);
        ResourceType r3 = new ResourceType("r3", true);
        for (int i = 0; i < 2; i++)
            strongbox.addResource(r1);
        for (int i = 0; i < 3; i++)
            strongbox.addResource(r2);
        for (int i = 0; i < 5; i++)
            strongbox.addResource(r3);
        assertEquals(10, strongbox.getQuantity());
    }

    @Test
    void resourceQuantityOfStrongboxWithResourcesOfMultipleTypes() throws IllegalResourceTransferException {
        Strongbox strongbox = new Strongbox();
        ResourceType r1 = new ResourceType("r1", true);
        ResourceType r2 = new ResourceType("r2", true);
        ResourceType r3 = new ResourceType("r3", true);
        for (int i = 0; i < 2; i++)
            strongbox.addResource(r1);
        for (int i = 0; i < 3; i++)
            strongbox.addResource(r2);
        for (int i = 0; i < 5; i++)
            strongbox.addResource(r3);
        assertAll("getResourceQuantity",
                () -> assertEquals(2, strongbox.getResourceQuantity(r1)),
                () -> assertEquals(3, strongbox.getResourceQuantity(r2)),
                () -> assertEquals(5, strongbox.getResourceQuantity(r3))
        );
    }

    @ParameterizedTest
    @ValueSource(ints = { 1, 2, 3 })
    void quantityOfStrongboxWithResourcesOfSameTypeAndOneRemovedResource(int resourcesCount) throws IllegalResourceTransferException {
        Strongbox strongbox = new Strongbox();
        ResourceType r = new ResourceType("r", true);
        for (int i = 0; i < resourcesCount; i++)
            strongbox.addResource(r);
        strongbox.removeResource(r);
        assertEquals(resourcesCount - 1, strongbox.getQuantity());
    }

    @ParameterizedTest
    @ValueSource(ints = { 1, 2, 3 })
    void resourceQuantityOfStrongboxWithResourcesOfSameTypeAndOneRemovedResource(int resourcesCount) throws IllegalResourceTransferException {
        Strongbox strongbox = new Strongbox();
        ResourceType r = new ResourceType("r", true);
        for (int i = 0; i < resourcesCount; i++)
            strongbox.addResource(r);
        strongbox.removeResource(r);
        assertEquals(resourcesCount - 1, strongbox.getResourceQuantity(r));
    }

    @ParameterizedTest
    @ValueSource(ints = { 1, 2, 3 })
    void clearedStrongboxShouldBeEmpty(int resourcesCount) throws IllegalResourceTransferException {
        Strongbox strongbox = new Strongbox();
        ResourceType r = new ResourceType("r", true);
        for (int i = 0; i < resourcesCount; i++)
            strongbox.addResource(r);
        for (int i = 0; i < resourcesCount; i++)
            strongbox.removeResource(r);
        assertTrue(strongbox.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(ints = { 1, 2, 3 })
    void emptyStrongboxShouldNotBeAbleToRemoveResources(int resourcesCount) throws IllegalResourceTransferException {
        Strongbox strongbox = new Strongbox();
        ResourceType r = new ResourceType("r", true);
        for (int i = 0; i < resourcesCount; i++)
            strongbox.addResource(r);
        for (int i = 0; i < resourcesCount; i++)
            strongbox.removeResource(r);
        assertThrows(IllegalResourceTransferException.class, () -> strongbox.removeResource(r));
    }

    @Test
    void addAllFromStrongbox() throws IllegalResourceTransferException {
        ResourceType r1 = new ResourceType("r1", true);
        ResourceType r2 = new ResourceType("r2", true);
        ResourceType r3 = new ResourceType("r3", true);

        Strongbox strongbox = new Strongbox();
        for (int i = 0; i < 5; i++)
            strongbox.addResource(r1);
        for (int i = 0; i < 7; i++)
            strongbox.addResource(r2);

        ResourceContainer resContainer = new Strongbox();
        for (int i = 0; i < 2; i++)
            resContainer.addResource(r1);
        for (int i = 0; i < 3; i++)
            resContainer.addResource(r2);
        for (int i = 0; i < 5; i++)
            resContainer.addResource(r3);

        strongbox.addAll(resContainer);

        assertAll("strongbox",
                () -> assertEquals(Set.of(r1, r2, r3), strongbox.getResourceTypes()),
                () -> assertEquals(22, strongbox.getQuantity()),
                () -> assertAll("getResourceQuantity",
                        () -> assertEquals(7, strongbox.getResourceQuantity(r1)),
                        () -> assertEquals(10, strongbox.getResourceQuantity(r2)),
                        () -> assertEquals(5, strongbox.getResourceQuantity(r3))
                )
        );
    }
}
