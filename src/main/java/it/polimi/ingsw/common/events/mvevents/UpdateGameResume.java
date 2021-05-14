package it.polimi.ingsw.common.events.mvevents;


import it.polimi.ingsw.common.reducedmodel.*;

import java.util.List;

public class UpdateGameResume extends UpdateGameStart {
    private final boolean hasChosenLeaders;
    private final boolean hasChosenResources;

    public UpdateGameResume(List<String> players,
                            List<ReducedLeaderCard> leaderCards,
                            List<ReducedDevCard> developmentCards,
                            List<ReducedResourceContainer> resContainers,
                            List<ReducedResourceTransactionRecipe> productions,
                            int baseProduction,
                            List<ReducedActionToken> actionTokens,
                            List<Integer> leaders,
                            List<Integer> warehouseShelves,
                            int strongbox,
                            ReducedBoost boost,
                            int chosenLeadersCount,
                            boolean hasChosenLeaders,
                            boolean hasChosenResources) {
        super(players, leaderCards, developmentCards, resContainers, productions, baseProduction,
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
