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
 * This class represents a transaction of resources.
 */
public class ResourceTransaction {
    private final Game game;
    private final Player player;
    private final Map<ResourceContainer, Map<ResourceType, Integer>> inputContainers;
    private final Map<ResourceType, Integer> inputNonStorable;
    private final Map<ResourceContainer, Map<ResourceType, Integer>> outputContainers;
    private final Map<ResourceType, Integer> outputNonStorable;
    private final Map<ResourceType, Integer> discardedOutput;

    /**
     * Initializes a resource transaction.
     *
     * @param game                the game the player is playing in
     * @param player              the player on which to trigger the actions of the non-storable resources
     * @param transactionRequests the list of the transaction requests to execute
     */
    public ResourceTransaction(Game game, Player player, List<ResourceTransactionRequest> transactionRequests) {
        this.game = game;
        this.player = player;
        this.inputContainers = getInputContainers(transactionRequests);
        this.inputNonStorable = getInputNonStorable(transactionRequests);
        this.outputContainers = getOutputContainers(transactionRequests);
        this.outputNonStorable = getOutputNonStorable(transactionRequests);
        this.discardedOutput = getDiscardedOutput(transactionRequests);
    }

    private static Map<ResourceContainer, Map<ResourceType, Integer>> getInputContainers(List<ResourceTransactionRequest> transactionRequests) {
        return ResourceTransactionRequest.mergeContainerMaps(
                transactionRequests.stream().map(ResourceTransactionRequest::getInputContainers).toList());
    }

    private static Map<ResourceType, Integer> getInputNonStorable(List<ResourceTransactionRequest> transactionRequests) {
        return ResourceTransactionRequest.mergeResourceMaps(
                transactionRequests.stream().map(ResourceTransactionRequest::getInputNonStorable).toList());
    }

    private static Map<ResourceContainer, Map<ResourceType, Integer>> getOutputContainers(List<ResourceTransactionRequest> transactionRequests) {
        return ResourceTransactionRequest.mergeContainerMaps(
                transactionRequests.stream().map(ResourceTransactionRequest::getOutputContainers).toList());
    }

    private static Map<ResourceType, Integer> getOutputNonStorable(List<ResourceTransactionRequest> transactionRequests) {
        return ResourceTransactionRequest.mergeResourceMaps(
                transactionRequests.stream().map(ResourceTransactionRequest::getOutputNonStorable).toList());
    }

    private static Map<ResourceType, Integer> getDiscardedOutput(List<ResourceTransactionRequest> transactionRequests) {
        return ResourceTransactionRequest.mergeResourceMaps(
                transactionRequests.stream().map(ResourceTransactionRequest::getDiscardedOutput).toList());
    }

    /**
     * Get set of all resource containers, in input and in output
     *
     * @return the set of all containers
     */
    private Set<ResourceContainer> getAllContainers() {
        Set<ResourceContainer> allContainers = new HashSet<>();
        allContainers.addAll(inputContainers.keySet());
        allContainers.addAll(outputContainers.keySet());
        return allContainers;
    }

    /**
     * Get map of clones of all resource containers, in input and in output
     *
     * @return the map of all cloned containers
     */
    private Map<ResourceContainer, ResourceContainer> getClonedContainers() {
        Set<ResourceContainer> allContainers = getAllContainers();

        /* Make map of clones of all resource containers */
        Map<ResourceContainer, ResourceContainer> clonedContainers = allContainers.stream()
                .collect(Collectors.toMap(Function.identity(), ResourceContainer::copy));

        /* Make map of clones of all resource container groups */
        Map<ResourceContainerGroup, ResourceContainerGeneralGroup> clonedContainerGroups = allContainers.stream()
                .map(ResourceContainer::getGroup)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toMap(Function.identity(), ResourceContainerGeneralGroup::new, (g1, g2) -> g1));

        // TODO: Add tests of this with WarehouseShelf
        clonedContainerGroups.values().forEach(g -> g.getResourceContainers().forEach(c -> {
            if (clonedContainers.containsKey(c)) {
                g.replaceResourceContainer(c, clonedContainers.get(c));
                clonedContainers.get(c).setGroup(g);
            }
        }));

        return clonedContainers;
    }

    /**
     * Executes the transaction. Replaces the blanks in input and in output with the given resources, removes the input
     * storable resources from the given resource containers, adds the output storable resources into the given resource
     * containers, takes the non-storable input resources from the player and gives the non-storable output resources to
     * the player.
     * <p>
     * This is a transaction: if the transfer is unsuccessful, a checked exception is thrown and the states of the game,
     * the player and the resource containers remain unchanged.
     *
     * @throws IllegalResourceTransferException if the transaction failed
     */
    public void execute() throws IllegalResourceTransferException {
        Map<ResourceContainer, ResourceContainer> clonedContainers = getClonedContainers();

        /* Try removing all input storable resources from cloned resource containers.
         * If a resource transfer exception is thrown, the requested activation is not possible. */
        for (ResourceContainer resContainer : inputContainers.keySet())
            clonedContainers.get(resContainer).removeResources(inputContainers.get(resContainer));

        /* Try adding all output storable resources into cloned resource containers (with input removed).
         * If a resource transfer exception is thrown, the requested activation is not possible. */
        for (ResourceContainer resContainer : outputContainers.keySet())
            clonedContainers.get(resContainer).addResources(outputContainers.get(resContainer));

        /* Remove all input storable resources from real resource containers.
         * This is possible if it worked with cloned resource containers */
        try {
            for (ResourceContainer resContainer : inputContainers.keySet())
                resContainer.removeResources(inputContainers.get(resContainer));
        } catch (IllegalResourceTransferException e) {
            throw new RuntimeException(e);
        }

        /* Add all output storable resources into real resource containers.
         * This is possible if it worked with cloned resource containers. */
        try {
            for (ResourceContainer resContainer : outputContainers.keySet())
                resContainer.addResources(outputContainers.get(resContainer));
        } catch (IllegalResourceTransferException e) {
            throw new RuntimeException(e);
        }

        /* Discard the chosen resources to be discarded */
        player.discardResources(game, discardedOutput.values().stream().reduce(0, Integer::sum));

        /* Take all input non-storable resources from player */
        for (ResourceType resType : inputNonStorable.keySet())
            resType.takeFromPlayer(game, player, inputNonStorable.get(resType));

        /* Give all output non-storable resources to player */
        for (ResourceType resType : outputNonStorable.keySet())
            resType.giveToPlayer(game, player, outputNonStorable.get(resType));
    }
}
