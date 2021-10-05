package it.polimi.ingsw.common.backend.model;

import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.backend.model.leadercards.LeaderCard;
import it.polimi.ingsw.common.backend.model.resourcecontainers.IllegalResourceTransferException;
import it.polimi.ingsw.common.backend.model.resourcecontainers.Shelf;
import it.polimi.ingsw.common.backend.model.resourcetransactions.*;
import it.polimi.ingsw.common.backend.model.resourcetypes.ResourceType;
import it.polimi.ingsw.common.reducedmodel.ReducedPlayerSetup;

import java.util.List;
import java.util.Map;
import java.util.Set;

/** This class represents player setup in the early game. */
public class PlayerSetup {
    /** The starting faith points. */
    private final int initialFaithPoints;

    /** The number of leader cards that must be chosen during the setup. */
    private final int chosenLeadersCount;

    /** The number of choosable resources obtained at the beginning. */
    private final int initialResources;

    /** The resources that cannot be chosen. */
    private final Set<ResourceType> initialExcludedResources;

    /** <code>true</code> if the initial faith points have been given; <code>false</code> otherwise. */
    private boolean hasGivenInitialFaithPoints;

    /** <code>true</code> if the initial leader cards have been chosen; <code>false</code> otherwise. */
    private boolean hasChosenLeaders;

    /** <code>true</code> if the initial resources have been chosen; <code>false</code> otherwise. */
    private boolean hasChosenResources;

    public PlayerSetup(int initialFaithPoints, int chosenLeadersCount, int initialResources, Set<ResourceType> initialExcludedResources) {
        this.initialFaithPoints = initialFaithPoints;
        this.chosenLeadersCount = chosenLeadersCount;
        this.initialResources = initialResources;
        this.initialExcludedResources = Set.copyOf(initialExcludedResources);

        this.hasGivenInitialFaithPoints = false;
        this.hasChosenLeaders = false;
        this.hasChosenResources = initialResources == 0;
    }

    public int getInitialFaithPoints() {
        return initialFaithPoints;
    }

    public int getChosenLeadersCount() {
        return chosenLeadersCount;
    }

    public int getInitialResources() {
        return initialResources;
    }

    public Set<ResourceType> getInitialExcludedResources() {
        return initialExcludedResources;
    }

    public void giveInitialFaithPoints(Game game, Player player) throws CannotChooseException {
        if (hasGivenInitialFaithPoints)
            throw new CannotChooseException(0);

        player.incrementFaithPoints(game, initialFaithPoints);

        hasGivenInitialFaithPoints = true;
        if (isDone())
            game.onPlayerSetupDone();
    }

    /**
     * Chooses leaders from the hand of the player.
     *
     * @param view          the view to which the model dispatches the update
     * @param chosenLeaders the leader cards to choose
     * @throws CannotChooseException if the leader cards have already been chosen
     */
    public void chooseLeaders(View view, Game game, Player player, List<LeaderCard> chosenLeaders) throws CannotChooseException, IllegalArgumentException {
        if (hasChosenLeaders)
            throw new CannotChooseException(0);

        if (chosenLeaders.size() != chosenLeadersCount)
            throw new CannotChooseException(chosenLeadersCount - chosenLeaders.size());

        player.retainLeaders(view, chosenLeaders);

        hasChosenLeaders = true;
        if (isDone())
            game.onPlayerSetupDone();
    }

    /**
     * Chooses the initial resources to be given to the player.
     *
     * @param game    the game the player is playing in
     * @param player  the player
     * @param shelves the destination shelves
     * @throws CannotChooseException                           all the allowed initial resources have already been
     *                                                         chosen
     * @throws IllegalResourceTransactionReplacementsException
     * @throws IllegalResourceTransactionContainersException
     * @throws IllegalResourceTransferException                when trasferring resources to a container fails
     */
    public void chooseResources(Game game, Player player, Map<Shelf, Map<ResourceType, Integer>> shelves) throws CannotChooseException, IllegalResourceTransactionReplacementsException, IllegalResourceTransactionContainersException, IllegalResourceTransferException {
        if (hasChosenResources)
            throw new CannotChooseException(0);

        ResourceTransactionRecipe transactionRecipe = new ResourceTransactionRecipe(
                Map.of(), 0, Set.of(), Map.of(), initialResources, initialExcludedResources, false);

        ResourceTransactionRequest transactionRequest = new ResourceTransactionRequest(
                transactionRecipe, Map.of(), Map.of(), Map.copyOf(shelves), Map.of());

        new ResourceTransaction(game, player, List.of(transactionRequest)).execute();

        hasChosenResources = true;
        if (isDone())
            game.onPlayerSetupDone();
    }

    /**
     * Returns whether the player has chosen the leaders.
     *
     * @return <code>true</code> if the player has chosen the leaders; <code>false</code> otherwise.
     */
    public boolean hasChosenLeaders() {
        return hasChosenLeaders;
    }

    /**
     * Returns whether the initial resources have been chosen.
     *
     * @return <code>true</code> if the initial resources have been chosen; <code>false</code> otherwise.
     */
    public boolean hasChosenResources() {
        return hasChosenResources;
    }

    /**
     * Returns whether the setup is done.
     *
     * @return <code>true</code> if the setup is done; <code>false</code> otherwise.
     */
    public boolean isDone() {
        /* Last or necessary because setups are created by GSON -> constructor does not get called */
        return hasGivenInitialFaithPoints && hasChosenLeaders && (hasChosenResources || initialResources == 0);
    }

    public ReducedPlayerSetup reduce() {
        return new ReducedPlayerSetup(chosenLeadersCount, initialResources, initialExcludedResources.stream().map(ResourceType::getName).toList(), hasChosenLeaders, hasChosenResources);
    }
}
