package it.polimi.ingsw.common.reducedmodel;

import java.util.List;

public class ReducedGame {
    private List<ReducedActionToken> actionTokens;
    private List<ReducedResourceContainer> containers;
    private String currentPlayer;
    private List<ReducedDevCard> developmentCards;
    private ReducedDevCardGrid devCardGrid;
    private int faithPoints;
    private boolean lastRound = false;
    private List<ReducedLeaderCard> leaderCards;
    private int leadersToChoose = 2;
    private ReducedMarket market;
    private List<String> players;
    private List<ReducedResourceTransactionRecipe> productions;
    private int resourcesToChoose;
    private ReducedPlayerSetup setup;
    private List<Boolean> vaticanSections;
    private int victoryPoints;

    public void setActionTokens(List<ReducedActionToken> actionTokens) {
        this.actionTokens = actionTokens;
    }

    public void setContainer(ReducedResourceContainer newContainer) {
        ReducedResourceContainer toBeReplaced = containers.stream().filter(c -> c.getId() == newContainer.getId()).findAny().orElseThrow();
        containers.set(containers.indexOf(toBeReplaced), newContainer);
    }

    public void setContainers(List<ReducedResourceContainer> containers) {
        this.containers = containers;
    }

    public void setCurrentPlayer(String currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public void setDevelopmentCards(List<ReducedDevCard> developmentCards) {
        this.developmentCards = developmentCards;
    }

    public void setDevCardGrid(ReducedDevCardGrid devCardGrid) {
        this.devCardGrid = devCardGrid;
    }

    public void setFaithPoints(int faithPoints) {
        this.faithPoints = faithPoints;
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

    public void setLeaderCards(List<ReducedLeaderCard> leaderCards) {
        this.leaderCards = leaderCards;
    }

    public void setMarket(ReducedMarket mkt) {
        market = mkt;
    }

    public void setPlayers(List<String> players) {
        this.players = players;
    }

    public int getResourcesToChoose() {
        return resourcesToChoose;
    }

    public void setResourcesToChoose(int resourcesToChoose) {
        this.resourcesToChoose = resourcesToChoose;
    }

    public void setSetup(ReducedPlayerSetup setup) {
        this.setup = setup;
    }

    public void setVaticanSections(List<Boolean> vaticanSections) {
        this.vaticanSections = vaticanSections;
    }

    public void setVictoryPoints(int pts) {
        victoryPoints = pts;
    }
}
