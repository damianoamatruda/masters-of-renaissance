package it.polimi.ingsw.common.backend.model.resourcecontainers;

import it.polimi.ingsw.common.backend.model.resourcetypes.ResourceType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static it.polimi.ingsw.common.backend.model.resourcecontainers.Warehouse.WarehouseShelf;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Warehouse.
 */
public class WarehouseTest {
    @Test
    void emptyWarehouse() {
        Warehouse warehouse = new Warehouse(0);
        assertEquals(List.of(), warehouse.getShelves());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 3, 5})
    void growingSizeOfWarehouseShelves(int shelvesCount) {
        Warehouse warehouse = new Warehouse(shelvesCount);
        for (int i = 0; i < shelvesCount; i++)
            assertEquals(i + 1, warehouse.getShelves().get(i).getSize());
    }

    @Test
    void resourceTypesOfNewWarehouseShelf() {
        Warehouse warehouse = new Warehouse(1);
        WarehouseShelf warehouseShelf = warehouse.getShelves().get(0);
        assertEquals(Set.of(), warehouseShelf.getResourceTypes());
    }

    @Test
    void quantityOfNewWarehouseShelf() {
        Warehouse warehouse = new Warehouse(1);
        WarehouseShelf warehouseShelf = warehouse.getShelves().get(0);
        assertEquals(0, warehouseShelf.getQuantity());
    }

    @Test
    void resourceQuantityOfNewWarehouseShelf() {
        ResourceType r = new ResourceType("r", true);
        Warehouse warehouse = new Warehouse(1);
        WarehouseShelf warehouseShelf = warehouse.getShelves().get(0);
        assertEquals(0, warehouseShelf.getResourceQuantity(r));
    }

    @Test
    void resourceTypeOfNewWarehouseShelf() {
        Warehouse warehouse = new Warehouse(1);
        WarehouseShelf warehouseShelf = warehouse.getShelves().get(0);
        assertTrue(warehouseShelf.getResourceType().isEmpty());
    }

    @Test
    void newWarehouseShelfShouldBeEmpty() {
        Warehouse warehouse = new Warehouse(1);
        WarehouseShelf warehouseShelf = warehouse.getShelves().get(0);
        assertTrue(warehouseShelf.isEmpty());
    }

    @Test
    void newWarehouseShelfShouldNotBeFull() {
        Warehouse warehouse = new Warehouse(1);
        WarehouseShelf warehouseShelf = warehouse.getShelves().get(0);
        assertFalse(warehouseShelf.isFull());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void resourceTypesOfWarehouseShelf(int resourcesCount) throws IllegalResourceTransferException {
        ResourceType r = new ResourceType("r", true);
        Warehouse warehouse = new Warehouse(5);
        WarehouseShelf warehouseShelf = warehouse.getShelves().get(4);
        warehouseShelf.addResources(Map.of(r, resourcesCount));
        assertEquals(Set.of(r), warehouseShelf.getResourceTypes());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void quantityOfWarehouseShelf(int resourcesCount) throws IllegalResourceTransferException {
        ResourceType r = new ResourceType("r", true);
        Warehouse warehouse = new Warehouse(5);
        WarehouseShelf warehouseShelf = warehouse.getShelves().get(4);
        warehouseShelf.addResources(Map.of(r, resourcesCount));
        assertEquals(resourcesCount, warehouseShelf.getQuantity());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void resourceQuantityOfWarehouseShelf(int resourcesCount) throws IllegalResourceTransferException {
        ResourceType r = new ResourceType("r", true);
        Warehouse warehouse = new Warehouse(5);
        WarehouseShelf warehouseShelf = warehouse.getShelves().get(4);
        warehouseShelf.addResources(Map.of(r, resourcesCount));
        assertEquals(resourcesCount, warehouseShelf.getResourceQuantity(r));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void warehouseShelfShouldNotBeEmpty(int resourcesCount) throws IllegalResourceTransferException {
        ResourceType r = new ResourceType("r", true);
        Warehouse warehouse = new Warehouse(5);
        WarehouseShelf warehouseShelf = warehouse.getShelves().get(4);
        warehouseShelf.addResources(Map.of(r, resourcesCount));
        assertFalse(warehouseShelf.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void warehouseShelfShouldNotBeFull(int resourcesCount) throws IllegalResourceTransferException {
        ResourceType r = new ResourceType("r", true);
        Warehouse warehouse = new Warehouse(resourcesCount + 1);
        WarehouseShelf warehouseShelf = warehouse.getShelves().get(resourcesCount);
        warehouseShelf.addResources(Map.of(r, resourcesCount));
        assertFalse(warehouseShelf.isFull());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void warehouseShelfShouldBeFull(int resourcesCount) throws IllegalResourceTransferException {
        Warehouse warehouse = new Warehouse(resourcesCount);
        WarehouseShelf warehouseShelf = warehouse.getShelves().get(resourcesCount - 1);
        ResourceType r = new ResourceType("r", true);
        warehouseShelf.addResources(Map.of(r, resourcesCount));
        assertTrue(warehouseShelf.isFull());
    }

    @Test
    void warehouseShelfShouldNotBeAbleToAddResourceOfAnotherType() throws IllegalResourceTransferException {
        ResourceType r1 = new ResourceType("r1", true);
        ResourceType r2 = new ResourceType("r2", true);
        Warehouse warehouse = new Warehouse(5);
        WarehouseShelf warehouseShelf = warehouse.getShelves().get(4);
        warehouseShelf.addResources(Map.of(r1, 2));
        assertThrows(IllegalResourceTransferException.class, () -> warehouseShelf.addResources(Map.of(r2, 1)));
    }

    @Test
    void warehouseShelfShouldNotBeAbleToAddResourceInAnotherWarehouseShelf() throws IllegalResourceTransferException {
        ResourceType r = new ResourceType("r", true);
        Warehouse warehouse = new Warehouse(2);

        WarehouseShelf warehouseShelf1 = warehouse.getShelves().get(0);
        warehouseShelf1.addResources(Map.of(r, 1));

        WarehouseShelf warehouseShelf2 = warehouse.getShelves().get(1);
        assertThrows(IllegalResourceTransferException.class, () -> warehouseShelf2.addResources(Map.of(r, 1)));
    }

    @Test
    void warehouseShelfShouldNotBeAbleToRemoveResourceOfAnotherType() throws IllegalResourceTransferException {
        ResourceType r1 = new ResourceType("r1", true);
        ResourceType r2 = new ResourceType("r2", true);
        Warehouse warehouse = new Warehouse(5);
        WarehouseShelf warehouseShelf = warehouse.getShelves().get(4);
        warehouseShelf.addResources(Map.of(r1, 3));
        assertThrows(IllegalResourceTransferException.class, () -> warehouseShelf.removeResources(Map.of(r2, 1)));
    }

    @Test
    void emptyWarehouseShelfShouldNotBeAbleToRemoveResource() {
        ResourceType r = new ResourceType("r", true);
        Warehouse warehouse = new Warehouse(5);
        WarehouseShelf warehouseShelf = warehouse.getShelves().get(4);
        assertThrows(IllegalResourceTransferException.class, () -> warehouseShelf.removeResources(Map.of(r, 1)));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void quantityOfWarehouseShelfWithOneRemovedResource(int resourcesCount) throws IllegalResourceTransferException {
        ResourceType r = new ResourceType("r", true);
        Warehouse warehouse = new Warehouse(5);
        WarehouseShelf warehouseShelf = warehouse.getShelves().get(4);
        warehouseShelf.addResources(Map.of(r, resourcesCount));
        warehouseShelf.removeResources(Map.of(r, 1));
        assertEquals(resourcesCount - 1, warehouseShelf.getQuantity());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void resourceQuantityOfWarehouseShelfWithOneRemovedResource(int resourcesCount) throws IllegalResourceTransferException {
        ResourceType r = new ResourceType("r", true);
        Warehouse warehouse = new Warehouse(5);
        WarehouseShelf warehouseShelf = warehouse.getShelves().get(4);
        warehouseShelf.addResources(Map.of(r, resourcesCount));
        warehouseShelf.removeResources(Map.of(r, 1));
        assertEquals(resourcesCount - 1, warehouseShelf.getResourceQuantity(r));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void resourceTypesOfClearedWarehouseShelf(int resourcesCount) throws IllegalResourceTransferException {
        ResourceType r = new ResourceType("r", true);
        Warehouse warehouse = new Warehouse(3);
        WarehouseShelf warehouseShelf = warehouse.getShelves().get(2);
        warehouseShelf.addResources(Map.of(r, resourcesCount));
        warehouseShelf.removeResources(Map.of(r, resourcesCount));
        assertEquals(Set.of(), warehouseShelf.getResourceTypes());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void quantityOfClearedWarehouseShelf(int resourcesCount) throws IllegalResourceTransferException {
        ResourceType r = new ResourceType("r", true);
        Warehouse warehouse = new Warehouse(3);
        WarehouseShelf warehouseShelf = warehouse.getShelves().get(2);
        warehouseShelf.addResources(Map.of(r, resourcesCount));
        warehouseShelf.removeResources(Map.of(r, resourcesCount));
        assertEquals(0, warehouseShelf.getQuantity());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void resourceQuantityOfClearedWarehouseShelf(int resourcesCount) throws IllegalResourceTransferException {
        ResourceType r = new ResourceType("r", true);
        Warehouse warehouse = new Warehouse(3);
        WarehouseShelf warehouseShelf = warehouse.getShelves().get(2);
        warehouseShelf.addResources(Map.of(r, resourcesCount));
        warehouseShelf.removeResources(Map.of(r, resourcesCount));
        assertEquals(0, warehouseShelf.getResourceQuantity(r));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void resourceTypeOfClearedWarehouseShelf(int resourcesCount) throws IllegalResourceTransferException {
        ResourceType r = new ResourceType("r", true);
        Warehouse warehouse = new Warehouse(3);
        WarehouseShelf warehouseShelf = warehouse.getShelves().get(2);
        warehouseShelf.addResources(Map.of(r, resourcesCount));
        warehouseShelf.removeResources(Map.of(r, resourcesCount));
        assertTrue(warehouseShelf.getResourceType().isEmpty());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void clearedWarehouseShelfShouldBeEmpty(int resourcesCount) throws IllegalResourceTransferException {
        ResourceType r = new ResourceType("r", true);
        Warehouse warehouse = new Warehouse(3);
        WarehouseShelf warehouseShelf = warehouse.getShelves().get(2);
        warehouseShelf.addResources(Map.of(r, resourcesCount));
        warehouseShelf.removeResources(Map.of(r, resourcesCount));
        assertTrue(warehouseShelf.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void clearedShelfShouldNotBeFull(int resourcesCount) throws IllegalResourceTransferException {
        ResourceType r = new ResourceType("r", true);
        Warehouse warehouse = new Warehouse(3);
        WarehouseShelf warehouseShelf = warehouse.getShelves().get(2);
        warehouseShelf.addResources(Map.of(r, resourcesCount));
        warehouseShelf.removeResources(Map.of(r, resourcesCount));
        assertFalse(warehouseShelf.isFull());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void clearedShelfShouldNotBeAbleToRemoveResources(int resourcesCount) throws IllegalResourceTransferException {
        ResourceType r = new ResourceType("r", true);
        Warehouse warehouse = new Warehouse(5);
        WarehouseShelf warehouseShelf = warehouse.getShelves().get(4);
        warehouseShelf.addResources(Map.of(r, resourcesCount));
        warehouseShelf.removeResources(Map.of(r, resourcesCount));
        assertThrows(IllegalResourceTransferException.class, () -> warehouseShelf.removeResources(Map.of(r, 1)));
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void swapShouldBePossible(boolean direct) throws IllegalResourceTransferException {
        ResourceType r1 = new ResourceType("r1", true);
        ResourceType r2 = new ResourceType("r2", true);

        Warehouse warehouse = new Warehouse(13);

        WarehouseShelf warehouseShelf1 = warehouse.getShelves().get(6);
        warehouseShelf1.addResources(Map.of(r1, 3));

        WarehouseShelf warehouseShelf2 = warehouse.getShelves().get(12);
        warehouseShelf2.addResources(Map.of(r2, 5));

        if (direct)
            ResourceContainer.swap(warehouseShelf1, warehouseShelf2);
        else
            ResourceContainer.swap(warehouseShelf2, warehouseShelf1);

        assertAll("shelves",
                () -> assertAll("warehouseShelf1",
                        () -> assertEquals(7, warehouseShelf1.getSize()),
                        () -> assertEquals(Set.of(r2), warehouseShelf1.getResourceTypes()),
                        () -> assertEquals(5, warehouseShelf1.getQuantity()),
                        () -> assertEquals(5, warehouseShelf1.getResourceQuantity(r2))
                ),
                () -> assertAll("warehouseShelf2",
                        () -> assertEquals(13, warehouseShelf2.getSize()),
                        () -> assertEquals(Set.of(r1), warehouseShelf2.getResourceTypes()),
                        () -> assertEquals(3, warehouseShelf2.getQuantity()),
                        () -> assertEquals(3, warehouseShelf2.getResourceQuantity(r1))
                )
        );
    }

    @Test
    void swapShouldNotBePossibleBecauseOfQuantities() throws IllegalResourceTransferException {
        ResourceType r1 = new ResourceType("r1", true);
        ResourceType r2 = new ResourceType("r2", true);

        Warehouse warehouse = new Warehouse(6);

        WarehouseShelf warehouseShelf1 = warehouse.getShelves().get(2);
        warehouseShelf1.addResources(Map.of(r1, 3));

        WarehouseShelf warehouseShelf2 = warehouse.getShelves().get(4);
        warehouseShelf2.addResources(Map.of(r2, 5));

        assertThrows(IllegalResourceTransferException.class, () -> ResourceContainer.swap(warehouseShelf1, warehouseShelf2));
    }

    @Test
    void swapShouldNotBePossibleBecauseOfResourceTypes() throws IllegalResourceTransferException {
        ResourceType r1 = new ResourceType("r1", true);
        ResourceType r2 = new ResourceType("r2", true);

        Warehouse warehouse1 = new Warehouse(7);
        WarehouseShelf warehouseShelf1a = warehouse1.getShelves().get(5);
        warehouseShelf1a.addResources(Map.of(r1, 2));
        WarehouseShelf warehouseShelf1b = warehouse1.getShelves().get(6);
        warehouseShelf1b.addResources(Map.of(r2, 3));

        Warehouse warehouse2 = new Warehouse(13);
        WarehouseShelf warehouseShelf2 = warehouse2.getShelves().get(12);
        warehouseShelf2.addResources(Map.of(r1, 5));

        assertThrows(IllegalResourceTransferException.class, () -> ResourceContainer.swap(warehouseShelf1b, warehouseShelf2));
    }

    @Test
    void swapTwice() throws IllegalResourceTransferException {
        ResourceType r1 = new ResourceType("r1", true);
        ResourceType r2 = new ResourceType("r2", true);

        Warehouse warehouse = new Warehouse(13);

        WarehouseShelf warehouseShelf1 = warehouse.getShelves().get(6);
        warehouseShelf1.addResources(Map.of(r1, 3));

        WarehouseShelf warehouseShelf2 = warehouse.getShelves().get(12);
        warehouseShelf2.addResources(Map.of(r2, 5));

        ResourceContainer.swap(warehouseShelf1, warehouseShelf2);
        ResourceContainer.swap(warehouseShelf1, warehouseShelf2);

        assertAll("shelves",
                () -> assertAll("warehouseShelf1",
                        () -> assertEquals(7, warehouseShelf1.getSize()),
                        () -> assertEquals(Set.of(r1), warehouseShelf1.getResourceTypes()),
                        () -> assertEquals(3, warehouseShelf1.getQuantity()),
                        () -> assertEquals(3, warehouseShelf1.getResourceQuantity(r1))
                ),
                () -> assertAll("warehouseShelf2",
                        () -> assertEquals(13, warehouseShelf2.getSize()),
                        () -> assertEquals(Set.of(r2), warehouseShelf2.getResourceTypes()),
                        () -> assertEquals(5, warehouseShelf2.getQuantity()),
                        () -> assertEquals(5, warehouseShelf2.getResourceQuantity(r2))
                )
        );
    }
}
