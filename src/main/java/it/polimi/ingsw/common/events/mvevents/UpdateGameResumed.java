package it.polimi.ingsw.common.events.mvevents;

import it.polimi.ingsw.common.backend.model.actiontokens.ActionToken;
import it.polimi.ingsw.common.backend.model.resourcetransactions.ResourceTransactionRecipe;
import it.polimi.ingsw.common.reducedmodel.ReducedDevCard;
import it.polimi.ingsw.common.reducedmodel.ReducedDevCardGrid;
import it.polimi.ingsw.common.reducedmodel.ReducedLeaderCard;
import it.polimi.ingsw.common.reducedmodel.ReducedMarket;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceContainer;

import java.util.List;

public class UpdateGameResumed extends UpdateGameStart {
    private final boolean hasChosenLeaders, hasChosenResources;

    public UpdateGameResumed(
            List<String> nicknames,
            ReducedMarket market,
            ReducedDevCardGrid developmentCardGrid,
            List<ReducedLeaderCard> leaderCards,
            List<ReducedDevCard> developmentCards,
            List<ReducedResourceContainer> resContainers,
            List<ResourceTransactionRecipe> productions,
            List<Integer> leaders,
            List<Integer> warehouseShelves,
            int strongbox,
            List<ActionToken> actionTokens,
            boolean hasChosenLeaders,
            boolean hasChosenResources
    ) {
        super(nicknames, market, developmentCardGrid, leaderCards, developmentCards, resContainers, productions, leaders, warehouseShelves, strongbox, actionTokens);
        this.hasChosenLeaders = hasChosenLeaders;
        this.hasChosenResources = hasChosenResources;
    }

    public boolean hasChosenLeaders() {
        return hasChosenLeaders;
    }

    public boolean hasChosenResources() {
        return hasChosenResources;
    }
}
