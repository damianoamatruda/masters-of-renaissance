package it.polimi.ingsw.common.backend.model;

import it.polimi.ingsw.common.backend.model.cardrequirements.DevCardRequirement;
import it.polimi.ingsw.common.backend.model.cardrequirements.ResourceRequirement;
import it.polimi.ingsw.common.backend.model.resourcecontainers.IllegalResourceTransferException;
import it.polimi.ingsw.common.backend.model.resourcecontainers.ResourceContainer;
import it.polimi.ingsw.common.backend.model.resourcetransactions.*;
import it.polimi.ingsw.common.backend.model.resourcetypes.ResourceType;
import it.polimi.ingsw.common.reducedmodel.ReducedDevCard;

import java.util.List;
import java.util.Map;

/**
 * Development cards allow the player to produce resources. They can be bought in exchange for resources and their
 * level/color can make up part of a leader card's activation requirements.
 *
 * @see Card
 * @see DevCardRequirement
 * @see ResourceTransactionRecipe
 */
public class DevelopmentCard extends Card {
    private final DevCardColor color;
    private final ResourceRequirement cost;
    private final int level;
    private final ResourceTransactionRecipe production;

    /**
     * Class constructor.
     *
     * @param color         the color of the card
     * @param level         the level of the card
     * @param cost          the resources needed to buy the card
     * @param production    the production associated with the card
     * @param victoryPoints the victory points associated with the card
     * @param id            the card id
     */
    public DevelopmentCard(DevCardColor color, int level, ResourceRequirement cost, ResourceTransactionRecipe production, int victoryPoints, int id) {
        super(victoryPoints, id);

        if (cost == null)
            throw new NullPointerException();

        this.color = color;
        this.cost = cost;
        this.level = level;
        this.production = production;
    }

    /**
     * @return the card's color
     */
    public DevCardColor getColor() {
        return color;
    }

    /**
     * @return the card's cost
     */
    public ResourceRequirement getCost() {
        return cost;
    }

    /**
     * @return the card's level
     */
    public int getLevel() {
        return level;
    }

    /**
     * @return the card's production
     */
    public ResourceTransactionRecipe getProduction() {
        return production;
    }

    /**
     * Takes the resources specified in the card's cost from the player's.
     *
     * @param game          the game the player is playing in
     * @param player        the player to assign the card to and to take the resources from
     * @param resContainers selection map specifying where to take the resources from
     * @throws IllegalResourceTransferException if the player cannot pay for the card
     */
    public void takeFromPlayer(Game game, Player player, Map<ResourceContainer, Map<ResourceType, Integer>> resContainers) throws IllegalResourceTransactionReplacementsException, IllegalResourceTransactionContainersException, IllegalResourceTransferException {
        ResourceTransactionRecipe transactionRecipe = new ResourceTransactionRecipe(
                cost.getDiscountedCost(player), 0, Map.of(), 0);

        ResourceTransactionRequest transactionRequest = new ResourceTransactionRequest(
                transactionRecipe, resContainers, Map.of(), Map.of(), Map.of());

        new ResourceTransaction(game, player, List.of(transactionRequest)).execute();
    }

    public ReducedDevCard reduce() {
        return new ReducedDevCard(getId(), getVictoryPoints(), getColor().getName(), getCost().reduceRR(), getLevel(), getProduction().getId());
    }
}
