package it.polimi.ingsw.common.backend.model.resourcecontainers;

import it.polimi.ingsw.common.backend.model.resourcetypes.ResourceType;

/**
 * Exception thrown when a resource cannot be added or removed.
 */
public class IllegalResourceTransferException extends Exception {
    /**
     * Constructs an <code>IllegalResourceTransferException</code> that is thrown because
     * a container is bound to a different resource type.
     * 
     * @param res the resource being added/removed.
     * @param isAdded whether the resource is being added to the container (false for removal).
     * @param boundRes resource binding the container.
     */
    public IllegalResourceTransferException(ResourceType res, boolean isAdded, ResourceType boundRes) {
        super(String.format("Cannot %s resource of type %s to shelf bound to type %s",
            isAdded ? "add" : "remove", res.getName(), boundRes.getName()));
    }

    /**
     * Constructs an <code>IllegalResourceTransferException</code> that is thrown because
     * a non-storable resource trying to be stored
     * 
     * @param nonStorable  the non-storable resource being added/removed.
     * @param isAdded whether the resource is being added to the container (false for removal).
     */
    public IllegalResourceTransferException(ResourceType nonStorable, boolean isAdded) {
        super(String.format("Cannot %s resource of type %s from shelf: resource type is non-storable",
            isAdded ? "add" : "remove", nonStorable.getName()));
    }

    /**
     * Constructs an <code>IllegalResourceTransferException</code> that is thrown because
     * a container's capacity is hitting a limit (empty/full).
     * 
     * @param res the resource being added/removed.
     * @param isAdded whether the resource is being added to the container (false for removal).
     * @param s the <code>ResourceContainer</code> involved in the operation.
     */
    public IllegalResourceTransferException(ResourceType res, boolean isAdded, ResourceContainer s) {
        super(String.format("Cannot %s resource of type %s: choosen container is %s",
            isAdded ? "add" : "remove", res.getName(), s.getResourceQuantity(res) == 0 ? "empty" : "full"));
    }

    /**
     * Constructs an <code>IllegalResourceTransferException</code> that is thrown because
     * a resource is trying to be added to an unboud warehouse shelf when
     * there's another shelf already bound to that resource type. 
     * 
     * @param res the resource being added/removed.
     * @param shelfIndex the conflicting warehouse shelf's index.
     */
    public IllegalResourceTransferException(ResourceType res, int shelfIndex) {
        super(String.format("Cannot add resource of type %s: a warehouse shelf %d is already bound to that resource",
            res.getName(), shelfIndex));
    }
}
