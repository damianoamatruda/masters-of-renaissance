package it.polimi.ingsw.common.events.mvevents;

import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.backend.model.actiontokens.ActionToken;
import it.polimi.ingsw.common.reducedmodel.ReducedDevCard;
import it.polimi.ingsw.common.reducedmodel.ReducedDevCardGrid;
import it.polimi.ingsw.common.reducedmodel.ReducedLeaderCard;
import it.polimi.ingsw.common.reducedmodel.ReducedMarket;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceContainer;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceTransactionRecipe;

import java.util.List;

public class UpdateGameStart implements MVEvent {
    // TODO reduce, boosts, commproto docs
    private final List<String> nicknames;
    private final ReducedMarket market;
    private final ReducedDevCardGrid developmentCardGrid;
    private final List<ReducedLeaderCard> leaderCards;
    private final List<ReducedDevCard> developmentCards;
    private final List<ReducedResourceContainer> resContainers;
    private final List<ReducedResourceTransactionRecipe> productions;
    private final List<Integer> leaders, warehouseShelves;
    private final int strongbox;
    private final List<ActionToken> actionTokens;

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
     * @param warehouseShelves      player's warehouseShelves' IDs
     * @param strongbox             player's strongbox's ID
     */
    public UpdateGameStart(
            List<String> nicknames,
            ReducedMarket market,
            ReducedDevCardGrid developmentCardGrid,
            List<ReducedLeaderCard> leaderCards,
            List<ReducedDevCard> developmentCards,
            List<ReducedResourceContainer> resContainers,
            List<ReducedResourceTransactionRecipe> productions,
            List<Integer> leaders,
            List<Integer> warehouseShelves,
            int strongbox,
            List<ActionToken> actionTokens) {
        
        this.nicknames = nicknames;
        this.market = market;
        this.developmentCardGrid = developmentCardGrid;
        this.leaderCards = leaderCards;
        this.developmentCards = developmentCards;
        this.resContainers = resContainers;
        this.productions = productions;
        this.leaders = leaders;
        this.warehouseShelves = warehouseShelves;
        this.strongbox = strongbox;
        this.actionTokens = actionTokens;
    }

    /**
     * @return the players' nicknames
     */
    public List<String> getNicknames() {
        return nicknames;
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
     * @return the action tokens
     */
    public List<ActionToken> getActionTokens() {
        return actionTokens;
    }


    @Override
    public void handle(View view) {
        view.update(this);
    }

    
}
