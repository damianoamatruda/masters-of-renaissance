package it.polimi.ingsw.common.events.mvevents;

import it.polimi.ingsw.common.reducedmodel.*;

import java.util.List;

public class UpdateGameResume extends UpdateGameStart {
    public UpdateGameResume(List<String> players,
                            List<ReducedLeaderCard> leaderCards,
                            List<ReducedDevCard> developmentCards,
                            List<ReducedResourceContainer> resContainers,
                            List<ReducedResourceTransactionRecipe> productions,
                            List<ReducedResourceType> resourceTypes, List<ReducedColor> colors, List<ReducedActionToken> actionTokens) {
        super(players, leaderCards, developmentCards, resContainers, productions, resourceTypes, colors, actionTokens);
    }
}
