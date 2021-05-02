package it.polimi.ingsw.server.model.resourcecontainers;

import it.polimi.ingsw.server.model.resourcetypes.ResourceType;

/**
 * Exception thrown when a resource cannot be added or removed.
 */
public class IllegalResourceTransferException extends Exception {
    /**
     * Class constructor.
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
     * Class constructor.
     * 
     * @param nonStorable  the non-storable resource being added/removed.
     * @param isAdded whether the resource is being added to the container (false for removal).
     */
    public IllegalResourceTransferException(ResourceType nonStorable, boolean isAdded) {
        super(String.format("Cannot %s resource of type %s from shelf: resource type is non-storable",
            isAdded ? "add" : "remove", nonStorable.getName()));
    }

    /**
     * Class constructor.
     * 
     * @param res the resource being added/removed.
     * @param isAdded whether the resource is being added to the container (false for removal).
     * @param s the <code>ResourceContainer</code> involved in the operation.
     */
    public IllegalResourceTransferException(ResourceType res, boolean isAdded, ResourceContainer s) {
        super(String.format("Cannot %s resource of type %s: choosen container is %s",
            isAdded ? "add" : "remove", res.getName(), s.getResourceQuantity(res) == 0 ? "empty" : "full"));
    }
}
