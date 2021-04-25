package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.resourcecontainers.IllegalResourceTransferException;
import it.polimi.ingsw.server.model.resourcecontainers.ResourceContainer;
import it.polimi.ingsw.server.model.resourcetypes.ResourceType;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This class represents a group of multiple requested contemporary productions.
 */
public class ProductionGroup {
    /** The immutable list of the requested productions to activate. */
    private final List<ProductionRequest> productionRequests;

    /**
     * Initializes a production group.
     *
     * @param productionRequests the list of the requested productions to activate
     */
    public ProductionGroup(List<ProductionRequest> productionRequests) {
        this.productionRequests = List.copyOf(productionRequests);
    }

    /**
     * Activates the requested productions. Replaces the blanks in input and in output with the given resources, removes
     * the input storable resources from the given resource containers, adds the output storable resources into the
     * given resource containers, takes the non-storable input resources from the player and gives the non-storable
     * output resources to the player.
     * <p>
     * This is a transaction: if the transfer is unsuccessful, a checked exception is thrown and the states of the game,
     * the player and the resource containers remain unchanged.
     *
     * @param game   the game the player is playing in
     * @param player the player on which to trigger the actions of the non-storable resources
     * @throws IllegalProductionActivationException if the transaction failed
     */
    public void activate(Game game, Player player) throws IllegalProductionActivationException {
        if (!productionRequests.stream().allMatch(ProductionRequest::isValid))
            throw new IllegalProductionActivationException("Not all production requests are valid.");

        /* Get set of all resource containers, in input and in output */
        Set<ResourceContainer> allContainers = new HashSet<>();
        productionRequests.stream()
                .map(ProductionRequest::getInputContainers)
                .map(Map::keySet)
                .forEach(allContainers::addAll);
        productionRequests.stream()
                .map(ProductionRequest::getOutputContainers)
                .map(Map::keySet)
                .forEach(allContainers::addAll);

        /* Make map of clones of all resource containers */
        Map<ResourceContainer, ResourceContainer> clonedContainers = allContainers.stream()
                .collect(Collectors.toMap(Function.identity(), ResourceContainer::copy));

        /* If a resource transfer exception is thrown inside this block, the requested activation is not possible */
        for (ProductionRequest productionReq : productionRequests) {
            /* Try removing all input storable resources from cloned resource containers */
            for (ResourceContainer resContainer : productionReq.getInputContainers().keySet())
                for (ResourceType resType : productionReq.getInputContainers().get(resContainer).keySet())
                    for (int i = 0; i < productionReq.getInputContainers().get(resContainer).get(resType); i++)
                        try {
                            clonedContainers.get(resContainer).removeResource(resType);
                        } catch (IllegalResourceTransferException e) {
                            throw new IllegalProductionActivationException("Illegal input transfer.", e);
                        }

            /* Try adding all output storable resources into cloned resource containers (with input removed) */
            for (ResourceContainer resContainer : productionReq.getOutputContainers().keySet())
                for (ResourceType resType : productionReq.getOutputContainers().get(resContainer).keySet())
                    for (int i = 0; i < productionReq.getOutputContainers().get(resContainer).get(resType); i++)
                        try {
                            clonedContainers.get(resContainer).addResource(resType);
                        } catch (IllegalResourceTransferException e) {
                            if (!productionReq.getProduction().hasDiscardableOutput())
                                throw new IllegalProductionActivationException("Illegal output transfer.", e);
                        }
        }

        /* This should be possible, as it worked with cloned resource containers */
        for (ProductionRequest productionReq : productionRequests) {
            /* Remove all input storable resources from real resource containers */
            for (ResourceContainer resContainer : productionReq.getInputContainers().keySet())
                for (ResourceType resType : productionReq.getInputContainers().get(resContainer).keySet())
                    for (int i = 0; i < productionReq.getInputContainers().get(resContainer).get(resType); i++)
                        try {
                            resContainer.removeResource(resType);
                        } catch (IllegalResourceTransferException e) {
                            throw new RuntimeException("Implementation error when removing all input storable resources from real resource containers", e);
                        }

            /* Add all output storable resources into real resource containers */
            for (ResourceContainer resContainer : productionReq.getOutputContainers().keySet())
                for (ResourceType resType : productionReq.getOutputContainers().get(resContainer).keySet())
                    for (int i = 0; i < productionReq.getOutputContainers().get(resContainer).get(resType); i++)
                        try {
                            resContainer.addResource(resType);
                        } catch (IllegalResourceTransferException e) {
                            if (productionReq.getProduction().hasDiscardableOutput())
                                player.discardResource(game, resType);
                            else
                                throw new RuntimeException("Implementation error when adding all output storable resources into real resource containers.", e);
                        }

            /* Filter the non-storable resources */
            Map<ResourceType, Integer> nonStorableReplacedInput = productionReq.getReplacedInput().entrySet().stream()
                    .filter(e -> !e.getKey().isStorable())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            Map<ResourceType, Integer> nonStorableReplacedOutput = productionReq.getReplacedOutput().entrySet().stream()
                    .filter(e -> !e.getKey().isStorable())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            /* Take all input non-storable resources from player; this is always possible */
            for (ResourceType resType : nonStorableReplacedInput.keySet())
                for (int i = 0; i < nonStorableReplacedInput.get(resType); i++)
                    resType.takeFromPlayer(game, player);

            /* Give all output non-storable resources to player; this is always possible */
            for (ResourceType resType : nonStorableReplacedOutput.keySet())
                for (int i = 0; i < nonStorableReplacedOutput.get(resType); i++)
                    resType.giveToPlayer(game, player);
        }
    }

    /**
     * This class represents a production request.
     */
    public static class ProductionRequest {
        private final Production production;
        private final Map<ResourceType, Integer> inputBlanksRep;
        private final Map<ResourceType, Integer> outputBlanksRep;
        private final Map<ResourceContainer, Map<ResourceType, Integer>> inputContainers;
        private final Map<ResourceContainer, Map<ResourceType, Integer>> outputContainers;

        /**
         * Initializes a production request.
         *
         * @param production       the production to use
         * @param inputBlanksRep   the map of the resources chosen as replacement for blanks in input
         * @param outputBlanksRep  the map of the resources chosen as replacement for blanks in output
         * @param inputContainers  the map of the resource containers from which to remove the input storable resources
         * @param outputContainers the map of the resource containers into which to add the output storable resources
         */
        public ProductionRequest(Production production,
                                 Map<ResourceType, Integer> inputBlanksRep,
                                 Map<ResourceType, Integer> outputBlanksRep,
                                 Map<ResourceContainer, Map<ResourceType, Integer>> inputContainers,
                                 Map<ResourceContainer, Map<ResourceType, Integer>> outputContainers) {
            this.production = production;
            this.inputBlanksRep = Map.copyOf(inputBlanksRep);
            this.outputBlanksRep = Map.copyOf(outputBlanksRep);
            this.inputContainers = Map.copyOf(inputContainers);
            this.outputContainers = Map.copyOf(outputContainers);
        }

        /**
         * Checks that resource containers are given for respectively all non-storable resources in a given map.
         *
         * @param resourceMap   the map of resources
         * @param resContainers the map of the resource containers to use for all the resources
         * @return <code>true</code> if the resources and the quantities match; <code>false</code> otherwise.
         */
        private static boolean checkContainers(Map<ResourceType, Integer> resourceMap,
                                               Map<ResourceContainer, Map<ResourceType, Integer>> resContainers) {
            /* Filter the storable resources */
            resourceMap = resourceMap.entrySet().stream()
                    .filter(e -> e.getKey().isStorable())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            /* Check that the map of resources and the map of resource containers contain the same number of storable
               resources */
            int resourceMapResourcesCount = resourceMap.values().stream().reduce(0, Integer::sum);
            int resContainersResourcesCount = resContainers.values().stream()
                    .map(m -> m.values().stream().reduce(0, Integer::sum))
                    .reduce(0, Integer::sum);
            if (resContainersResourcesCount != resourceMapResourcesCount)
                return false;

            /* Check that the quantity of each storable resource type in the map of resources is the same as in the map
               of resource containers */
            for (ResourceType resType : resourceMap.keySet()) {
                int resourceCount = 0;
                for (ResourceContainer resContainer : resContainers.keySet())
                    resourceCount += resContainers.get(resContainer).getOrDefault(resType, 0);
                if (resourceCount != resourceMap.get(resType))
                    return false;
            }

            return true;
        }

        /**
         * Returns whether the production request is valid, i.e.:
         * <ol>
         *     <li>maps of input and output replacement resources have the same number of resources as input and output
         *         blanks</li>
         *     <li>maps of input and output replacement resources do not contain excluded resources</li>
         *     <li>maps of input and output resource containers are given for respectively all non-storable resources in
         *         input and output</li>
         * </ol>
         *
         * @return <code>true</code> if the production request is valid; <code>false</code> otherwise.
         */
        public boolean isValid() {
            if (!inputBlanksRep.isEmpty() && inputBlanksRep.values().stream().reduce(0, Integer::sum) != production.getInputBlanks())
                return false;
            if (!outputBlanksRep.isEmpty() && outputBlanksRep.values().stream().reduce(0, Integer::sum) != production.getOutputBlanks())
                return false;

            if (inputBlanksRep.keySet().stream().anyMatch(resType -> production.getInputBlanksExclusions().contains(resType)))
                return false;
            if (outputBlanksRep.keySet().stream().anyMatch(resType -> production.getOutputBlanksExclusions().contains(resType)))
                return false;

            return checkContainers(getReplacedInput(), inputContainers) && checkContainers(getReplacedOutput(), outputContainers);
        }

        /**
         * Returns the requested production.
         *
         * @return the production
         */
        public Production getProduction() {
            return production;
        }

        /**
         * Builds an input resource map with replaced blanks.
         *
         * @return a map of resources including replaced blanks
         */
        public Map<ResourceType, Integer> getReplacedInput() {
            Map<ResourceType, Integer> replacedInput = new HashMap<>(production.getInput());
            inputBlanksRep.forEach((r, q) -> replacedInput.merge(r, q, Integer::sum));
            return Map.copyOf(replacedInput);
        }

        /**
         * Builds an output resource map with replaced blanks.
         *
         * @return a map of resources including replaced blanks
         */
        public Map<ResourceType, Integer> getReplacedOutput() {
            Map<ResourceType, Integer> replacedOutput = new HashMap<>(production.getOutput());
            outputBlanksRep.forEach((r, q) -> replacedOutput.merge(r, q, Integer::sum));
            return Map.copyOf(replacedOutput);
        }

        /**
         * Returns the requested input resource containers.
         *
         * @return the input resource containers
         */
        public Map<ResourceContainer, Map<ResourceType, Integer>> getInputContainers() {
            return inputContainers;
        }

        /**
         * Returns the requested output resource containers.
         *
         * @return the output resource containers
         */
        public Map<ResourceContainer, Map<ResourceType, Integer>> getOutputContainers() {
            return outputContainers;
        }
    }
}
