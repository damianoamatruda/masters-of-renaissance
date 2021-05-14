package it.polimi.ingsw.common.events.mvevents;

import it.polimi.ingsw.common.reducedmodel.*;

import java.util.List;
import java.util.Map;

public class UpdateGameResume extends UpdateGameStart {
    public UpdateGameResume(List<String> players,
                            List<ReducedLeaderCard> leaderCards,
                            List<ReducedDevCard> developmentCards,
                            Map<ReducedResourceContainer, String> resContainers,
                            List<ReducedResourceTransactionRecipe> productions,
                            int baseProduction,
                            List<ReducedActionToken> actionTokens,
                            List<Integer> leaders,
                            List<Integer> warehouseShelves,
                            int strongbox,
                            ReducedPlayerSetup playerSetup) {
        super(players, leaderCards, developmentCards, resContainers, productions, baseProduction, actionTokens, leaders, warehouseShelves, strongbox, playerSetup);
    }
}
