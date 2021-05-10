package it.polimi.ingsw.common.events;

import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.backend.model.DevelopmentCard;
import it.polimi.ingsw.common.backend.model.leadercards.LeaderCard;
import it.polimi.ingsw.common.backend.model.resourcecontainers.ResourceContainer;
import it.polimi.ingsw.common.backend.model.resourcetransactions.ResourceTransactionRecipe;
import it.polimi.ingsw.common.reducedmodel.ReducedDevCardGrid;
import it.polimi.ingsw.common.reducedmodel.ReducedMarket;

import java.util.List;

public class UpdateGameStart implements MVEvent {
    // TODO nicknames, actiontokens, commproto docs
    private final ReducedMarket market;
    private final ReducedDevCardGrid developmentCardGrid;
    private final List<LeaderCard> leaderCards;
    private final List<DevelopmentCard> developmentCards;
    private final List<ResourceContainer> resContainers;
    private final List<ResourceTransactionRecipe> productions;
    private final List<Integer> leaders, shelves; // TODO rename warehouseshelves
    private final int strongbox;

    /**
     * Class constructor.
     * 
     * @param market                market state
     * @param developmentCardGrid   development card grid state
     * @param leaderCards           leader cards available at play time
     * @param developmentCards      development cards available at play time
     * @param resContainers         resource containers available at play time
     * @param productions           productions available at play time
     * @param leaders               player's leaders' IDs
     * @param shelves               player's shelves' IDs
     * @param strongbox             player's strongbox's ID
     */
    public UpdateGameStart(
            ReducedMarket market,
            ReducedDevCardGrid developmentCardGrid,
            List<LeaderCard> leaderCards,
            List<DevelopmentCard> developmentCards,
            List<ResourceContainer> resContainers,
            List<ResourceTransactionRecipe> productions,
            List<Integer> leaders,
            List<Integer> shelves,
            int strongbox) {
        this.market = market;
        this.developmentCardGrid = developmentCardGrid;
        this.leaderCards = leaderCards;
        this.developmentCards = developmentCards;
        this.resContainers = resContainers;
        this.productions = productions;
        this.leaders = leaders;
        this.shelves = shelves;
        this.strongbox = strongbox;
    }

    /**
     * @return the productions
     */
    public List<ResourceTransactionRecipe> getProductions() {
        return productions;
    }

    /**
     * @return the resource containers
     */
    public List<ResourceContainer> getResContainers() {
        return resContainers;
    }

    /**
     * @return the development cards
     */
    public List<DevelopmentCard> getDevelopmentCards() {
        return developmentCards;
    }

    /**
     * @return the leader cards
     */
    public List<LeaderCard> getLeaderCards() {
        return leaderCards;
    }

    /**
     * @return the reduced development card grid
     */
    public ReducedDevCardGrid getDevelopmentCardGrid() {
        return developmentCardGrid;
    }

    /**
     * @return the reduced market
     */
    public ReducedMarket getMarket() {
        return market;
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
    public List<Integer> getShelves() {
        return shelves;
    }

    /**
     * @return the player's leaders' IDs
     */
    public List<Integer> getLeaders() {
        return leaders;
    }

    @Override
    public void handle(View view) {
        view.update(this);
    }

    
}
