package it.polimi.ingsw.common.reducedmodel;

import java.util.List;
import java.util.Map;

public class ReducedGame {
    private List<ReducedActionToken> actionTokens;
    private List<ReducedResourceContainer> containers;
    private String currentPlayer;
    private List<ReducedDevCard> developmentCards;
    private ReducedDevCardGrid devCardGrid;
    private int faithPoints;
    private boolean lastRound = false;
    private List<ReducedLeaderCard> leaderCards;
    private int leadersToChoose;
    private ReducedMarket market;
    private List<String> players;
    private Map<String, Boolean> playerState;
    private List<ReducedResourceTransactionRecipe> productions;
    private int resourcesToChoose;
    private Map<String, ReducedPlayerSetup> setup;
    private List<Boolean> vaticanSections;
    private Map<String, Integer> victoryPoints;

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

    public List<ReducedLeaderCard> getLeaders() {
        return leaderCards;
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

    /**
     * @return map nickname-active status of the player
     */
    public Map<String, Boolean> getPlayerState() {
        return playerState;
    }

    public void setPlayerState(String player, boolean newState) {
        this.playerState.put(player, newState);
    }

    public void setProductions(List<ReducedResourceTransactionRecipe> productions) {
        this.productions = productions;
    }

    public int getResourcesToChoose() {
        return resourcesToChoose;
    }

    public void setResourcesToChoose(int resourcesToChoose) {
        this.resourcesToChoose = resourcesToChoose;
    }

    public void setSetup(String player, ReducedPlayerSetup newSetup) {
        this.setup.put(player, newSetup);
    }

    public void setVaticanSections(List<Boolean> vaticanSections) {
        this.vaticanSections = vaticanSections;
    }

    public Map<String, Integer> getVictoryPoints() {
        return victoryPoints;
    }

    public void setVictoryPoints(String player, int pts) {
        victoryPoints.put(player, pts);
    }
}
