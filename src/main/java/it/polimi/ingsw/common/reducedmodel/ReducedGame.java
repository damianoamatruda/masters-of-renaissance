package it.polimi.ingsw.common.reducedmodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.polimi.ingsw.client.ReducedObjectPrinter;

public class ReducedGame {
    private final ReducedObjectPrinter printer;

    private String nickname;
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
    private final Map<String, Boolean> playerState;
    private final Map<String, List<Integer>> playerLeaders;
    private final Map<String, Integer> playerLeadersCount;
    private List<ReducedResourceTransactionRecipe> productions;
    private int resourcesToChoose;
    private Map<String, ReducedPlayerSetup> setup;
    private List<Boolean> vaticanSections;
    private final Map<String, Integer> victoryPoints;

    public ReducedGame(ReducedObjectPrinter printer) {
        this.printer = printer;

        actionTokens = new ArrayList<>();
        containers = new ArrayList<>();
        developmentCards = new ArrayList<>();
        players = new ArrayList<>();
        playerState = new HashMap<>();
        playerLeaders = new HashMap<>();
        playerLeadersCount = new HashMap<>();
        productions = new ArrayList<>();
        setup = new HashMap<>();
        vaticanSections = new ArrayList<>();
        victoryPoints = new HashMap<>();
    }

    public void setActionTokens(List<ReducedActionToken> actionTokens) {
        this.actionTokens = actionTokens;
    }

    public void setContainer(ReducedResourceContainer newContainer) {
        ReducedResourceContainer toBeReplaced = containers.stream().filter(c -> c.getId() == newContainer.getId()).findAny().orElseThrow();
        containers.set(containers.indexOf(toBeReplaced), newContainer);

        printer.update(newContainer);
    }

    public void setContainers(List<ReducedResourceContainer> containers) {
        this.containers = containers;

        containers.forEach(c -> printer.update(c));
    }

    public String getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(String currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public void setDevelopmentCards(List<ReducedDevCard> developmentCards) {
        this.developmentCards = developmentCards;

        developmentCards.forEach(c -> printer.update(c));
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

        leaderCards.forEach(l -> printer.update(l));
    }

    public void setMarket(ReducedMarket mkt) {
        market = mkt;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }


    public void setPlayers(List<String> players) {
        this.players = players;

    }

    public void setPlayerState(String player, boolean newState) {
        this.playerState.put(player, newState);
    }

    public void setPlayerLeaders(String player, int leaderId) {
        playerLeaders.compute(player, (k, v) -> {
            List<Integer> ids;
            if (v == null)
                ids = new ArrayList<>();
            else
                ids = v;
            
            ids.add(leaderId);
            return ids;
        });
    }

    public void setPlayerLeadersCount(String player, int count) {
        this.playerLeadersCount.put(player, count);
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

    public void setVictoryPoints(String player, int pts) {
        victoryPoints.put(player, pts);
    }

    public void setActiveVaticanSection(int vaticanSection) {
        vaticanSections.set(vaticanSection, true);
    }

    public List<ReducedResourceContainer> getContainers() {
        return containers;
    }
}
