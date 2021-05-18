package it.polimi.ingsw.common.reducedmodel;

import java.util.List;

public class ReducedGame {
    List<ReducedActionToken> actionTokens;
    List<ReducedLeaderCard> leaderCards;
    ReducedDevCardGrid devCardGrid;
    ReducedMarket market;
    List<ReducedResourceContainer> containers;
    ReducedPlayerSetup setup;
    List<Boolean> vaticanSections;
    int faithPoints;
    int victoryPoints;

    public void setVictoryPoints(int pts) {
        victoryPoints = pts;
    }

    public void setMarket(ReducedMarket mkt) {
        market = mkt;
    }

    public void setContainer(ReducedResourceContainer cont) {
        ReducedResourceContainer toBeReplaced = containers.stream().filter(c -> c.getId() == cont.getId()).findAny().orElseThrow();
        containers.set(containers.indexOf(toBeReplaced), cont);
    }
}
