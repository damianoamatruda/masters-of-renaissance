package it.polimi.ingsw.client.viewmodel;

import it.polimi.ingsw.common.reducedmodel.*;

import java.util.*;
import java.util.Map.Entry;

/** Data storage cache on the Masters Of Renaissance client. */
public class ViewModel {
    /** 
     * Player data.
     * 
     * E.g. ownership of objects, victory points...
     */
    private final Map<String, PlayerData> playerData;

    private String localPlayerNickname = "";
    private boolean isResumedGame;
    private boolean isSetupDone;

    private List<ReducedActionToken> actionTokens;
    private int blackCrossFP;
    private List<ReducedResourceContainer> containers;
    private String currentPlayer = "";
    private List<ReducedColor> devCardColors;
    private ReducedDevCardGrid devCardGrid;
    private List<ReducedDevCard> developmentCards;
    private ReducedFaithTrack faithTrack;
    private boolean isLastRound;
    private List<ReducedLeaderCard> leaderCards;
    private ReducedMarket market;
    private List<String> playerNicknames;
    private List<ReducedResourceTransactionRecipe> productions;
    private List<ReducedResourceType> resourceTypes;
    private String winner = "";
    private int slotsCount;
    private ReducedActionToken latestToken;

    private final List<String> clientColors = List.of("\u001B[92m", "\u001B[94m", "\u001B[95m", "\u001B[96m");
    private Map<String, String> mappedColors;
    private boolean isGameEnded = false;

    /**
     * Class constructor.
     * Initializes empty objects.
     */
    public ViewModel() {
        actionTokens = new ArrayList<>();
        containers = new ArrayList<>();
        devCardColors = new ArrayList<>();
        developmentCards = new ArrayList<>();
        leaderCards = new ArrayList<>();
        playerNicknames = new ArrayList<>();
        productions = new ArrayList<>();
        resourceTypes = new ArrayList<>();

        this.playerData = new HashMap<>();

        isLastRound = false;
        isSetupDone = false;

        mappedColors = new HashMap<>();
    }

    /**
     * @param nickname the nickname of the player whose data needs to be retrieved.
     * @return the playerData of the specified player
     */
    public Optional<PlayerData> getPlayerData(String nickname) {
        return Optional.ofNullable(playerData.get(nickname));
    }
    
    public Optional<PlayerData> getCurrentPlayerData() {
        return getPlayerData(getCurrentPlayer());
    }

    public Optional<PlayerData> getLocalPlayerData() {
        return getPlayerData(getLocalPlayerNickname());
    }
    
    /**
     * To be used when receiving the first UpdatePlayer message.
     * 
     * @param playerData the player's data
     */
    public void setPlayerData(String nickname, PlayerData playerData) {
        this.playerData.put(nickname, playerData);
    }

    /**
     * @param nickname
     * @return the topmost development cards in the player's slots
     */
    public Optional<ReducedResourceTransactionRecipe> getPlayerBaseProduction(String nickname) {
        if (!playerData.containsKey(nickname))
            return Optional.empty();

        return getProduction(playerData.get(nickname).getBaseProduction());
    }

    /**
     * @param nickname
     * @return the topmost development cards in the player's slots
     */
    public List<List<Optional<ReducedDevCard>>> getPlayerDevelopmentCards(String nickname) {
        if (!playerData.containsKey(nickname))
            return new ArrayList<>();

        return playerData.get(nickname).getDevSlots().stream()
            .map(slot -> slot.stream().map(c -> getDevelopmentCard(c)).toList())
            .toList();
    }

    /**
     * @param nickname the player whose faith points are to be returned
     * @return the faith points of the selected player
     */
    public int getPlayerFaithPoints(String nickname) {
        if (!playerData.containsKey(nickname))
            return 0;
        
        return playerData.get(nickname).getFaithPoints();
    }

    /**
     * @param nickname the player whose state to be returned
     * @return the state of the selected player
     */
    public boolean isPlayerActive(String nickname) {
        if (!playerData.containsKey(nickname))
            return false;
        
        return playerData.get(nickname).isActive();
    }

    /**
     * @param nickname
     * @return the reduced leader cards owned by the player
     */
    public List<ReducedLeaderCard> getPlayerLeaderCards(String nickname) {
        if (!playerData.containsKey(nickname))
            return new ArrayList<>();

        return playerData.get(nickname).getLeadersHand().stream()
                .map(id -> getLeaderCard(id).orElse(null))
                .filter(Objects::nonNull)
                .toList();
    }
    
    /**
     * @param nickname
     * @return the number of leader cards owned by the player
     */
    public int getPlayerLeadersCount(String nickname) {
        if (!playerData.containsKey(nickname))
            return 0;

        return playerData.get(nickname).getLeadersCount();
    }

    /**
     * @param nickname
     * @return the reduced recipes owned by the player, including:
     *          <ul>
     *              <li>the base production
     *              <li>the development cards productions
     *              <li>the leader cards productions
     *          </ul>
     */
    public List<ReducedResourceTransactionRecipe> getPlayerProductions(String nickname) {
        if (!playerData.containsKey(nickname))
            return new ArrayList<>();
        
        PlayerData pd = playerData.get(nickname);
        List<Integer> ids = new ArrayList<>();
        
        ids.add(pd.getBaseProduction());
        getPlayerLeaderCards(nickname).forEach(c -> {
            if (c.getProduction() >= 0 && c.isActive())
                ids.add(c.getProduction());
        });
        getPlayerDevelopmentCards(nickname).forEach(l -> {
            l.get(0).ifPresent(c -> {
                if (c.getProduction() >= 0)
                    ids.add(c.getProduction());
            });
        });

        return ids.stream()
            .map(id -> getProduction(id).orElse(null))
            .filter(Objects::nonNull)
            .toList();
    }

    /**
     * @param nickname
     * @return the player's warehouse shelves and depots
     */
    public List<ReducedResourceContainer> getPlayerShelves(String nickname) {
        if (!playerData.containsKey(nickname))
            return new ArrayList<>();

        List<ReducedResourceContainer> containers = new ArrayList<>();
        containers.addAll(getPlayerWarehouseShelves(nickname));
        containers.addAll(getPlayerDepots(nickname));
        return containers;
    }

    /**
     * @param nickname
     * @return the player's warehouse shelves
     */
    public List<ReducedResourceContainer> getPlayerWarehouseShelves(String nickname) {
        if (!playerData.containsKey(nickname))
            return new ArrayList<>();

        return playerData.get(nickname).getWarehouseShelves().stream()
                .map(this::getContainer)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    /**
     * @param nickname
     * @return the player's active leaders' depots
     */
    public List<ReducedResourceContainer> getPlayerDepots(String nickname) {
        if (!playerData.containsKey(nickname))
            return new ArrayList<>();

        return getPlayerLeaderCards(nickname).stream()
                .filter(ReducedLeaderCard::isActive)
                .map(ReducedLeaderCard::getContainerId)
                .map(this::getContainer)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    public Optional<ReducedResourceContainer> getPlayerStrongbox(String nickname) {
        if (!playerData.containsKey(nickname))
            return Optional.empty();

        return getContainer(playerData.get(nickname).getStrongbox());
    }

    public int getPlayerVictoryPoints(String nickname) {
        if (!playerData.containsKey(nickname))
            return 0;

        return playerData.get(nickname).getVictoryPoints();
    }

    /**
     * @param id the ID of the token to be returned
     * @return the token associated with the ID
     */
    public Optional<ReducedActionToken> getActionToken(int id) {
        return actionTokens.stream().filter(t -> t.getId() == id).findAny();
    }

    /**
     * @param actionTokens the actionTokens to set
     */
    public void setActionTokens(List<ReducedActionToken> actionTokens) {
        if (actionTokens != null)
            this.actionTokens = new ArrayList<>(actionTokens);
    }

    /**
     * @return blackCross' faith points
     */
    public int getBlackCrossFP() {
        return blackCrossFP;
    }

    /**
     * @param blackCrossFP the blackCross faith points to set
     */
    public void setBlackCrossFP(int blackCrossFP) {
        this.blackCrossFP = blackCrossFP;
    }

    /**
     * @param id the id of the container to be returned
     * @return the container corresponding to the id
     */
    public Optional<ReducedResourceContainer> getContainer(int id) {
        return containers.stream().filter(c -> c.getId() == id).findAny();
    }

    /**
     * @return the containers
     */
    public List<ReducedResourceContainer> getContainers() {
        return containers;
    }

    /**
     * @param containers the containers to set
     */
    public void setContainers(List<ReducedResourceContainer> containers) {
        if (containers != null)
            this.containers = new ArrayList<>(containers);
    }

    /**
     * @param container the container to set
     */
    public void setContainer(ReducedResourceContainer container) {
        containers.replaceAll(c -> c.getId() == container.getId() ? container : c);
    }

    /**
     * @return the currentPlayer
     */
    public String getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * @param currentPlayer the currentPlayer to set
     */
    public void setCurrentPlayer(String currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    /**
     * @return the devCardColors
     */
    public List<ReducedColor> getDevCardColors() {
        return devCardColors;
    }

    /**
     * @param devCardColors the devCardColors to set
     */
    public void setDevCardColors(List<ReducedColor> devCardColors) {
        if (devCardColors != null)
            this.devCardColors = new ArrayList<>(devCardColors);
    }

    /**
     * @return the devCardGrid
     */
    public Optional<ReducedDevCardGrid> getDevCardGrid() {
        return Optional.ofNullable(devCardGrid);
    }

    /**
     * @param devCardGrid the devCardGrid to set
     */
    public void setDevCardGrid(ReducedDevCardGrid devCardGrid) {
        this.devCardGrid = devCardGrid;
    }

    /**
     * @param id the ID of the card to be returned
     * @return the developmentCard matching the ID
     */
    public Optional<ReducedDevCard> getDevelopmentCard(int id) {
        return developmentCards.stream().filter(c -> c.getId() == id).findAny();
    }

    /**
     * @param color the card's color
     * @param level the card's level
     * @return the card of the specified color and level
     */
    public Optional<ReducedDevCard> getDevelopmentCard(String color, int level) {
        return developmentCards.stream().filter(c -> c.getColor().equals(color) && c.getLevel() == level).findAny();
    }

    /**
     * @param developmentCards the developmentCards to set
     */
    public void setDevelopmentCards(List<ReducedDevCard> developmentCards) {
        if (developmentCards != null)
            this.developmentCards = new ArrayList<>(developmentCards);
    }

    /**
     * @return the faithTrack
     */
    public Optional<ReducedFaithTrack> getFaithTrack() {
        return Optional.ofNullable(faithTrack);
    }

    /**
     * @param faithTrack the faithTrack to set
     */
    public void setFaithTrack(ReducedFaithTrack faithTrack) {
        this.faithTrack = faithTrack;
    }

    /**
     * @return whether it's the last round of the match
     */
    public boolean isLastRound() {
        return isLastRound;
    }

    /**
     * Sets last round to true.
     */
    public void setLastRound() {
        this.isLastRound = true;
    }

    /**
     * @param id the ID of the card to be returned
     * @return the leaderCard matching the ID
     */
    public Optional<ReducedLeaderCard> getLeaderCard(int id) {
        return leaderCards.stream().filter(c -> c.getId() == id).findAny();
    }

    /**
     * @param id the ID of the card to be activated
     */
    public void activateLeaderCard(int id) {
        leaderCards.replaceAll(l -> l.getId() == id ? l.getActivated() : l);
    }

    /**
     * @param leaderCards the leaderCards to set
     */
    public void setLeaderCards(List<ReducedLeaderCard> leaderCards) {
        if (leaderCards != null)
            this.leaderCards = new ArrayList<>(leaderCards);
    }

    /**
     * @return the market
     */
    public Optional<ReducedMarket> getMarket() {
        return Optional.ofNullable(market);
    }

    /**
     * @param market the market to set
     */
    public void setMarket(ReducedMarket market) {
        this.market = market;
    }

    /**
     * @return the playerNicknames
     */
    public List<String> getPlayerNicknames() {
        return playerNicknames;
    }

    /**
     * @param playerNicknames the playerNicknames to set
     */
    public void setPlayerNicknames(List<String> playerNicknames) {
        if (this.playerNicknames != null)
            this.playerNicknames = new ArrayList<>(playerNicknames);
            
        for(int i = 0; i < playerNicknames.size(); i++)
            mappedColors.put(playerNicknames.get(i), clientColors.get(i));
    }

    /**
     * @param id the ID of the production to be returned
     * @return the reduced production (transaction recipe)
     */
     public Optional<ReducedResourceTransactionRecipe> getProduction(int id) {
        return productions.stream().filter(p -> p.getId() == id).findAny();
    }

    /**
     * @param productions the productions to set
     */
    public void setProductions(List<ReducedResourceTransactionRecipe> productions) {
        if (productions != null)
            this.productions = new ArrayList<>(productions);
    }

    /**
     * @return the resourceTypes
     */
    public List<ReducedResourceType> getResourceTypes() {
        return resourceTypes;
    }

    /**
     * @param resourceTypes the resourceTypes to set
     */
    public void setResourceTypes(List<ReducedResourceType> resourceTypes) {
        if (resourceTypes!= null)
            this.resourceTypes = new ArrayList<>(resourceTypes);
    }

    /**
     * @return the vaticanSections
     */
    public Map<Integer, ReducedVaticanSection> getVaticanSections() {
        return getFaithTrack().isEmpty() ? new HashMap<>() : getFaithTrack().get().getVaticanSections();
    }

    /**
     * @param id the ID of the activated section
     */
    public void setVaticanSection(int id) {
        if (faithTrack != null)
            faithTrack.getVaticanSections().entrySet().stream()
                .map(Entry::getValue)
                .filter(vs -> vs.getId() == id).findAny().ifPresent(vs -> vs.setActive());
    }

    /**
     * @return the winner
     */
    public String getWinner() {
        return winner;
    }

    /**
     * @param winner the winner to set
     */
    public void setWinner(String winner) {
        this.winner = winner;
        this.isGameEnded = true;
    }

    /**
     * @return whether the game is resumed
     */
    public boolean isResumedGame() {
        return isResumedGame;
    }

    /**
     * @param isSetupDone whether the player setup is done
     */
    public void setSetupDone(boolean isSetupDone) {
        this.isSetupDone = isSetupDone;
    }

    /**
     * @return whether the player setup is done
     */
    public boolean isSetupDone() {
        return isResumedGame || isSetupDone; // when resuming setup is still false
    }

    /**
     * @param isResumedGame whether the game is resumed
     */
    public void setResumedGame(boolean isResumedGame) {
        this.isResumedGame = isResumedGame;
    }

    /**
     * @return the local player's nickname
     */
    public String getLocalPlayerNickname() {
        return localPlayerNickname;
    }

    /**
     * @param localPlayerNickname the nickname to set
     */
    public void setLocalPlayerNickname(String localPlayerNickname) {
        this.localPlayerNickname = localPlayerNickname;
    }

    public int getSlotsCount() {
        return slotsCount;
    }

    public void setSlotsCount(int slotsCount) {
        this.slotsCount = slotsCount;
    }

    public Optional<ReducedActionToken> getLatestToken() {
        return Optional.ofNullable(latestToken);
    }

    public void setLatestToken(ReducedActionToken latestToken) {
        this.latestToken = latestToken;
    }

    public String getClientColor(String nick) {
        return mappedColors.get(nick);
    }

    public boolean isGameEnded() {
        return isGameEnded;
    }
}
