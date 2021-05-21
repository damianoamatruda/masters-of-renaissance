package it.polimi.ingsw.common.reducedmodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import it.polimi.ingsw.client.ReducedObjectPrinter;

public class ReducedGame {
    private ReducedObjectPrinter printer;

    private String nickname;
    private List<ReducedActionToken> actionTokens;
    private Map<String, Integer> baseProductions;
    private List<ReducedResourceContainer> containers;
    private String currentPlayer;
    private List<ReducedDevCard> developmentCards;
    private ReducedDevCardGrid devCardGrid;
    private int faithPoints;
    private boolean lastRound = false;
    private List<ReducedLeaderCard> leaderCards;
    private ReducedMarket market;
    private List<String> players;
    private final Map<String, Boolean> playerState;
    private final Map<String, List<Integer>> playerLeaders;
    private final Map<String, Integer> playerLeadersCount;
    private final Map<String, Integer> playerStrongbox;
    private final Map<String, List<Integer>> playerWarehouseShelves;
    private List<ReducedResourceTransactionRecipe> productions;
    private Map<String, ReducedPlayerSetup> setup;
    private List<Boolean> vaticanSections;
    private final Map<String, Integer> victoryPoints;
    private String winner;

    public ReducedGame() {
        actionTokens = new ArrayList<>();
        baseProductions = new HashMap<>();
        containers = new ArrayList<>();
        developmentCards = new ArrayList<>();
        players = new ArrayList<>();
        playerState = new HashMap<>();
        playerLeaders = new HashMap<>();
        playerLeadersCount = new HashMap<>();
        playerStrongbox = new HashMap<>();
        playerWarehouseShelves = new HashMap<>();
        productions = new ArrayList<>();
        setup = new HashMap<>();
        vaticanSections = new ArrayList<>();
        victoryPoints = new HashMap<>();
    }

    public void setPrinter(ReducedObjectPrinter printer) {
        this.printer = printer;
    }

    public ReducedActionToken getActionToken(int actionToken) {
        return actionTokens.stream().filter(t -> t.getId() == actionToken).findAny().orElse(new ReducedActionToken(-1, "", ""));
    }

    public void setActionTokens(List<ReducedActionToken> actionTokens) {
        this.actionTokens = actionTokens;

        if(actionTokens != null)
            actionTokens.forEach(printer::update);
    }

    public void setBaseProduction(String player, int baseProd) {
        baseProductions.put(player, baseProd);

        printer.showBaseProductions(baseProductions);
    }

    public void setContainer(ReducedResourceContainer newContainer) {
        ReducedResourceContainer toBeReplaced = containers.stream().filter(c -> c.getId() == newContainer.getId()).findAny().orElseThrow();
        containers.set(containers.indexOf(toBeReplaced), newContainer);

        printer.update(newContainer);
    }

    public void setContainers(List<ReducedResourceContainer> containers) {
        this.containers = containers;

        containers.forEach(printer::update);
    }

    public String getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(String currentPlayer) {
        this.currentPlayer = currentPlayer;

        printer.showCurrentPlayer(currentPlayer);
    }

    public ReducedDevCard getDevCard(int id) {
        return developmentCards.stream().filter(rc -> rc.getId() == id).findAny().orElseThrow();
    }

    public void setDevelopmentCards(List<ReducedDevCard> developmentCards) {
        this.developmentCards = developmentCards;

        developmentCards.forEach(printer::update);
    }

    public ReducedDevCardGrid getDevCardGrid() {
        return devCardGrid;
    }

    public void setDevCardGrid(ReducedDevCardGrid devCardGrid) {
        this.devCardGrid = devCardGrid;

        printer.update(devCardGrid);
    }

    public void setFaithPoints(int faithPoints) {
        this.faithPoints = faithPoints;
    }

    public void setLastRound() {
        lastRound = true;
    }

    public int getLeadersToChoose() {
        return this.setup.get(nickname).getChosenLeadersCount();
    }

    public void setLeaderCards(List<ReducedLeaderCard> leaderCards) {
        this.leaderCards = leaderCards;

        leaderCards.forEach(printer::update);
    }

    public void setMarket(ReducedMarket mkt) {
        market = mkt;

        printer.update(mkt);
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }


    public void setPlayers(List<String> players) {
        this.players = players;

        printer.showPlayers(players);
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

        printer.showLeadersHand(player, leaderId);
    }

    public void setPlayerLeadersCount(String player, int count) {
        this.playerLeadersCount.put(player, count);
    }
    
    public int getPlayerStrongboxID(String player) {
        return this.playerStrongbox.get(player);
    }
    
    public void setPlayerStrongbox(String player, int strongboxId) {
        this.playerStrongbox.put(player, strongboxId);
    }

    public List<Integer> getPlayerWarehouseShelvesIDs(String player) {
        return playerWarehouseShelves.get(player);
    }

    public void setPlayerWarehouseShelves(String player, List<Integer> shelves) {
        this.playerWarehouseShelves.put(player, shelves);
    }

    /**
     * @param id the id of the production to be returned
     * @return   the production associated to the id
     */
    public Optional<ReducedResourceTransactionRecipe> getProduction(int id) {
        return productions.stream().filter(p -> p.getId() == id).findAny();
    }

    public void setProductions(List<ReducedResourceTransactionRecipe> productions) {
        this.productions = productions;
    }

    /**
     * @return the number of initial resources the player can choose
     */
    public int getResourcesToChoose() {
        return this.setup.get(nickname).getInitialResources();
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

    /**
     * @return all resource containers
     */
    public List<ReducedResourceContainer> getContainers() {
        return containers;
    }

    /**
     * @return all leader cards
     */
    public List<ReducedLeaderCard> getLeaderCards() {
        return leaderCards;
    }

    public boolean isLastRound() {
        return lastRound;
    }

    public void setLastRound(boolean lastRound) {
        this.lastRound = lastRound;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public String getWinner() {
        return winner;
    }

    /**
     * @param nickname the player to get the victory points of
     * @return         the player's victory points
     */
    public int getVictoryPoints(String nickname) {
        return victoryPoints.get(nickname);
    }
}
