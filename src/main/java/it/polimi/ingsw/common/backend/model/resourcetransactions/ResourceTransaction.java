package it.polimi.ingsw.common.backend.model.resourcetransactions;

import it.polimi.ingsw.common.backend.model.Game;
import it.polimi.ingsw.common.backend.model.Player;
import it.polimi.ingsw.common.backend.model.resourcecontainers.IllegalResourceTransferException;
import it.polimi.ingsw.common.backend.model.resourcecontainers.ResourceContainer;
import it.polimi.ingsw.common.backend.model.resourcecontainers.ResourceContainerGeneralGroup;
import it.polimi.ingsw.common.backend.model.resourcecontainers.ResourceContainerGroup;
import it.polimi.ingsw.common.backend.model.resourcetypes.ResourceType;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This class represents a transaction of multiple requests.
 */
public class ResourceTransaction {
    /** The immutable list of the transaction requests to activate. */
    private final List<ResourceTransactionRequest> transactionRequests;

    /**
     * Initializes a resource transaction.
     *
     * @param transactionRequests the list of the transaction requests to activate
     */
    public ResourceTransaction(List<ResourceTransactionRequest> transactionRequests) {
        this.transactionRequests = List.copyOf(transactionRequests);
    }

    /**
     * Activates the transaction requests. Replaces the blanks in input and in output with the given resources, removes
     * the input storable resources from the given resource containers, adds the output storable resources into the
     * given resource containers, takes the non-storable input resources from the player and gives the non-storable
     * output resources to the player.
     * <p>
     * This is a transaction: if the transfer is unsuccessful, a checked exception is thrown and the states of the game,
     * the player and the resource containers remain unchanged.
     *
     * @param game   the game the player is playing in
     * @param player the player on which to trigger the actions of the non-storable resources
     * @throws IllegalResourceTransferException if the transaction failed
     */
    public void activate(Game game, Player player) throws IllegalResourceTransferException {
        Map<ResourceType, Integer> inputNonStorable = new HashMap<>();
        Map<ResourceType, Integer> outputNonStorable = new HashMap<>();
        Map<ResourceType, Integer> discardedOutput = new HashMap<>();
        Map<ResourceContainer, Map<ResourceType, Integer>> inputContainers = new HashMap<>();
        Map<ResourceContainer, Map<ResourceType, Integer>> outputContainers = new HashMap<>();

        for (ResourceTransactionRequest request : transactionRequests) {
            request.getReplacedInput().entrySet().stream()
                    .filter(e -> !e.getKey().isStorable())
                    .forEach(e -> inputNonStorable.merge(e.getKey(), e.getValue(), Integer::sum));

            request.getReplacedOutput().entrySet().stream()
                    .filter(e -> !e.getKey().isStorable())
                    .forEach(e -> outputNonStorable.merge(e.getKey(), e.getValue(), Integer::sum));

            request.getDiscardedOutput().forEach((r, q) -> discardedOutput.merge(r, q, Integer::sum));

            request.getInputContainers().forEach((c, oldResMap) -> inputContainers.merge(c, oldResMap, (r1, r2) -> {
                r2.forEach((r, q) -> r1.merge(r, q, Integer::sum));
                return r1;
            }));

            request.getOutputContainers().forEach((c, oldResMap) -> outputContainers.merge(c, oldResMap, (r1, r2) -> {
                r2.forEach((r, q) -> r1.merge(r, q, Integer::sum));
                return r1;
            }));
        }

        /* Get set of all resource containers, in input and in output */
        Set<ResourceContainer> allContainers = new HashSet<>();
        allContainers.addAll(inputContainers.keySet());
        allContainers.addAll(outputContainers.keySet());

        /* Make map of clones of all resource containers */
        Map<ResourceContainer, ResourceContainer> clonedContainers = allContainers.stream()
                .collect(Collectors.toMap(Function.identity(), ResourceContainer::copy));

        /* Make map of clones of all resource container groups */
        Map<ResourceContainerGroup, ResourceContainerGeneralGroup> clonedContainerGroups = allContainers.stream()
                .map(ResourceContainer::getGroup)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toMap(Function.identity(), ResourceContainerGeneralGroup::new));

        // TODO: Add tests of this with WarehouseShelf
        clonedContainerGroups.values().forEach(g -> g.getResourceContainers().forEach(c -> {
            if (clonedContainers.containsKey(c)) {
                g.replaceResourceContainer(c, clonedContainers.get(c));
                clonedContainers.get(c).setGroup(g);
            }
        }));

        /* Try removing all input storable resources from cloned resource containers.
         * If a resource transfer exception is thrown, the requested activation is not possible. */
        for (ResourceContainer resContainer : inputContainers.keySet())
            clonedContainers.get(resContainer).removeResources(inputContainers.get(resContainer));

        /* Try adding all output storable resources into cloned resource containers (with input removed).
         * If a resource transfer exception is thrown, the requested activation is not possible. */
        for (ResourceContainer resContainer : outputContainers.keySet())
            clonedContainers.get(resContainer).addResources(outputContainers.get(resContainer));

        /* Remove all input storable resources from real resource containers.
         * This should be possible, as it worked with cloned resource containers */
        try {
            for (ResourceContainer resContainer : inputContainers.keySet())
                resContainer.removeResources(inputContainers.get(resContainer));
        } catch (IllegalResourceTransferException e) {
            throw new RuntimeException("Implementation error when removing all input storable resources from real resource containers", e);
        }

        /* Add all output storable resources into real resource containers.
         * This should be possible, as it worked with cloned resource containers. */
        try {
            for (ResourceContainer resContainer : outputContainers.keySet())
                resContainer.addResources(outputContainers.get(resContainer));
        } catch (IllegalResourceTransferException e) {
            throw new RuntimeException("Implementation error when adding all output storable resources into real resource containers.", e);
        }

        /* Discard the chosen resources to be discarded */
        player.discardResources(game, discardedOutput.values().stream().reduce(0, Integer::sum));

        /* Take all input non-storable resources from player; this is always possible */
        for (ResourceType resType : inputNonStorable.keySet())
            resType.takeFromPlayer(game, player, inputNonStorable.get(resType));

        /* Give all output non-storable resources to player; this is always possible */
        for (ResourceType resType : outputNonStorable.keySet())
            resType.giveToPlayer(game, player, outputNonStorable.get(resType));
    }
}
