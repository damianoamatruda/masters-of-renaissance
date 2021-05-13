package it.polimi.ingsw.common.events.mvevents;


import it.polimi.ingsw.common.reducedmodel.*;

import java.util.List;

public class UpdateGameResume extends UpdateGameStart {
    private final boolean hasChosenLeaders, hasChosenResources;

    public UpdateGameResume(
            List<String> nicknames,
            ReducedMarket market,
            ReducedDevCardGrid developmentCardGrid,
            List<ReducedLeaderCard> leaderCards,
            List<ReducedDevCard> developmentCards,
            List<ReducedResourceContainer> resContainers,
            List<ReducedResourceTransactionRecipe> productions,
            List<ReducedActionToken> actionTokens,
            List<Integer> leaders,
            List<Integer> warehouseShelves,
            int strongbox,
            boolean hasChosenLeaders,
            boolean hasChosenResources,
            ReducedBoost boost,
            int chosenLeadersCount
    ) {
        super(nicknames, market, developmentCardGrid, leaderCards, developmentCards, resContainers, productions,
              actionTokens, leaders, warehouseShelves, strongbox, boost, chosenLeadersCount);
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
