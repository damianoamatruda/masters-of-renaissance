package it.polimi.ingsw.common.reducedmodel;

import java.util.List;

public class ReducedGame {
    String currentPlayer;
    List<String> players;
    List<ReducedActionToken> actionTokens;
    List<ReducedLeaderCard> leaderCards;
    ReducedDevCardGrid devCardGrid;
    ReducedMarket market;
    List<ReducedResourceContainer> containers;
    ReducedPlayerSetup setup;
    List<Boolean> vaticanSections;
    int leadersToChoose = 2;
    int resourcesToChoose;
    int faithPoints;
    int victoryPoints;
    boolean lastRound = false;

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

    public void setContainers(List<ReducedResourceContainer> containers) {
        this.containers = containers;
    }

    public void setFaithPoints(int faithPoints) {
        this.faithPoints = faithPoints;
    }

    public void setActionTokens(List<ReducedActionToken> actionTokens) {
        this.actionTokens = actionTokens;
    }

    public void setDevCardGrid(ReducedDevCardGrid devCardGrid) {
        this.devCardGrid = devCardGrid;
    }

    public void setLeaderCards(List<ReducedLeaderCard> leaderCards) {
        this.leaderCards = leaderCards;
    }

    public void setPlayers(List<String> players) {
        this.players = players;
    }

    public void setSetup(ReducedPlayerSetup setup) {
        this.setup = setup;
    }

    public void setVaticanSections(List<Boolean> vaticanSections) {
        this.vaticanSections = vaticanSections;
    }

    public void setCurrentPlayer(String currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public void setLastRound() {
        lastRound = true;
    }

    public int getLeadersToChoose() {
        return leadersToChoose;
    }

    public void setLeadersToChoose(int leadersToChoose) {
        this.leadersToChoose = leadersToChoose;
    }

    public int getResourcesToChoose() {
        return resourcesToChoose;
    }

    public void setResourcesToChoose(int resourcesToChoose) {
        this.resourcesToChoose = resourcesToChoose;
    }
}
