package it.polimi.ingsw.common.events.mvevents;

import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.reducedmodel.*;

import java.util.List;

public class UpdateGameStart implements MVEvent {
    // TODO commproto docs
    private final List<String> players;
    private final List<ReducedLeaderCard> leaderCards;
    private final List<ReducedDevCard> developmentCards;
    private final List<ReducedResourceContainer> resContainers;
    private final List<ReducedResourceTransactionRecipe> productions;
    private final List<ReducedActionToken> actionTokens;
    private final List<Integer> leaders, warehouseShelves;
    private final int strongbox;
    private final ReducedBoost boost;
    private final int chosenLeadersCount;
    
    /**
     * Class constructor.
     *
     * @param leaderCards           leader cards available at play time
     * @param developmentCards      development cards available at play time
     * @param resContainers         resource containers available at play time
     * @param productions           productions available at play time
     * @param leaders               player's leaders' IDs
     * @param warehouseShelves      player's warehouseShelves' IDs
     * @param strongbox             player's strongbox's ID
     */
    public UpdateGameStart(List<String> players,
                           List<ReducedLeaderCard> leaderCards,
                           List<ReducedDevCard> developmentCards,
                           List<ReducedResourceContainer> resContainers,
                           List<ReducedResourceTransactionRecipe> productions,
                           List<ReducedActionToken> actionTokens,
                           List<Integer> leaders,
                           List<Integer> warehouseShelves,
                           int strongbox,
                           ReducedBoost boost,
                           int chosenLeadersCount) {
        this.players = players;
        this.leaderCards = leaderCards;
        this.developmentCards = developmentCards;
        this.resContainers = resContainers;
        this.productions = productions;
        this.actionTokens = actionTokens;
        this.leaders = leaders;
        this.warehouseShelves = warehouseShelves;
        this.strongbox = strongbox;
        this.boost = boost;
        this.chosenLeadersCount = chosenLeadersCount;
    }

    /**
     * @return the chosenLeadersCount
     */
    public int getChosenLeadersCount() {
        return chosenLeadersCount;
    }

    @Override
    public void handle(View view) {
        view.update(this);
    }

    /**
     * @return the players' nicknames
     */
    public List<String> getPlayers() {
        return players;
    }

    /**
     * @return the productions
     */
    public List<ReducedResourceTransactionRecipe> getProductions() {
        return productions;
    }

    /**
     * @return the resource containers
     */
    public List<ReducedResourceContainer> getResContainers() {
        return resContainers;
    }

    /**
     * @return the development cards
     */
    public List<ReducedDevCard> getDevelopmentCards() {
        return developmentCards;
    }

    /**
     * @return the leader cards
     */
    public List<ReducedLeaderCard> getLeaderCards() {
        return leaderCards;
    }

    /**
     * @return the action tokens
     */
    public List<ReducedActionToken> getActionTokens() {
        return actionTokens;
    }

    /**
     * @return the player's strongbox ID
     */
    public int getStrongbox() {
        return strongbox;
    }

    /**
     * @return the player's shelves' IDs
     */
    public List<Integer> getWarehouseShelves() {
        return warehouseShelves;
    }

    /**
     * @return the player's leaders' IDs
     */
    public List<Integer> getLeaders() {
        return leaders;
    }

    /**
     * @return the boost
     */
    public ReducedBoost getBoost() {
        return boost;
    }

}
