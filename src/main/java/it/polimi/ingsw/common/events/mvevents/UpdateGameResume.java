package it.polimi.ingsw.common.events.mvevents;

import it.polimi.ingsw.common.backend.model.actiontokens.ActionToken;
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
