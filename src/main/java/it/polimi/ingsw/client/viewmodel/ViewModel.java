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
    private Boolean isSetupDone;

    private List<ReducedActionToken> actionTokens;
    private int blackCrossFP;
    private List<ReducedResourceContainer> containers;
    private String currentPlayer;
    private List<ReducedColor> devCardColors;
    private ReducedDevCardGrid devCardGrid;
    private List<ReducedDevCard> developmentCards;
    private ReducedFaithTrack faithTrack;
    private boolean isGameEnded = false;
    private boolean isLastRound;
    private ReducedActionToken latestToken;
    private List<ReducedLeaderCard> leaderCards;
    private ReducedMarket market;
    private List<String> playerNicknames;
    private List<ReducedResourceTransactionRecipe> productions;
    private List<ReducedResourceType> resourceTypes;
    private int slotsCount;
    private String winner = "";

    private final List<String> clientColors = List.of("\u001B[92m", "\u001B[94m", "\u001B[95m", "\u001B[96m");
    private Map<String, String> mappedColors;

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

        mappedColors = new HashMap<>();
    }

    /**
     * @param nickname the nickname of the player whose data needs to be retrieved.
     * @return the playerData of the specified player
     */
    public synchronized Optional<PlayerData> getPlayerData(String nickname) {
        return Optional.ofNullable(playerData.get(nickname));
    }
    
    public synchronized Optional<PlayerData> getCurrentPlayerData() {
        return getPlayerData(getCurrentPlayer().orElse(null));
    }

    public synchronized Optional<PlayerData> getLocalPlayerData() {
        return getPlayerData(getLocalPlayerNickname());
    }
    
    /**
     * To be used when receiving the first UpdatePlayer message.
     * 
     * @param playerData the player's data
     */
    public synchronized void setPlayerData(String nickname, PlayerData playerData) {
        this.playerData.put(nickname, playerData);
    }

    /**
     * @param nickname
     * @return the topmost development cards in the player's slots
     */
    public synchronized Optional<ReducedResourceTransactionRecipe> getPlayerBaseProduction(String nickname) {
        if (!playerData.containsKey(nickname))
            return Optional.empty();

        return getProduction(playerData.get(nickname).getBaseProduction());
    }

    /**
     * @param nickname
     * @return all the development cards in the player's slots
     */
    public synchronized List<List<ReducedDevCard>> getPlayerDevelopmentCards(String nickname) {
        if (!playerData.containsKey(nickname))
            return new ArrayList<>();

        return playerData.get(nickname).getDevSlots().stream()
            .map(slot -> slot.stream()
                             .map(c -> getDevelopmentCard(c).orElse(null))
                             .filter(Objects::nonNull)
                             .toList())
            .toList();
    }

    /**
     * @param nickname
     * @return the topmost development cards in the player's slots
     */
    public synchronized List<ReducedDevCard> getPlayerDevelopmentSlots(String nickname) {
        if (!playerData.containsKey(nickname))
            return new ArrayList<>();

        return playerData.get(nickname).getDevSlots().stream()
            .map(slot -> slot.isEmpty() ? -1 : slot.get(0))
            .map(cardID -> getDevelopmentCard(cardID).orElse(null))
            .filter(Objects::nonNull)
            .toList();
    }

    /**
     * @param nickname the player whose faith points are to be returned
     * @return the faith points of the selected player
     */
    public synchronized int getPlayerFaithPoints(String nickname) {
        if (!playerData.containsKey(nickname))
            return 0;
        
        return playerData.get(nickname).getFaithPoints();
    }

    /**
     * @param nickname the player whose state to be returned
     * @return the state of the selected player
     */
    public synchronized boolean isPlayerActive(String nickname) {
        if (!playerData.containsKey(nickname))
            return false;
        
        return playerData.get(nickname).isActive();
    }

    /**
     * @param nickname
     * @return the reduced leader cards owned by the player
     */
    public synchronized List<ReducedLeaderCard> getPlayerLeaderCards(String nickname) {
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
    public synchronized int getPlayerLeadersCount(String nickname) {
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
    public synchronized List<ReducedResourceTransactionRecipe> getPlayerProductions(String nickname) {
        if (!playerData.containsKey(nickname))
            return new ArrayList<>();
        
        PlayerData pd = playerData.get(nickname);
        List<Integer> ids = new ArrayList<>();
        
        ids.add(pd.getBaseProduction());
        getPlayerLeaderCards(nickname).stream()
            .filter(ReducedLeaderCard::isActive)
            .forEach(c -> ids.add(c.getProduction()));
        getPlayerDevelopmentSlots(nickname)
            .forEach(c -> ids.add(c.getProduction()));

        return ids.stream()
            .map(id -> getProduction(id).orElse(null))
            .filter(Objects::nonNull)
            .toList();
    }

    /**
     * @param nickname
     * @return the player's warehouse shelves and depots
     */
    public synchronized List<ReducedResourceContainer> getPlayerShelves(String nickname) {
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
    public synchronized List<ReducedResourceContainer> getPlayerWarehouseShelves(String nickname) {
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
    public synchronized List<ReducedResourceContainer> getPlayerDepots(String nickname) {
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

    public synchronized Optional<ReducedResourceContainer> getPlayerStrongbox(String nickname) {
        if (!playerData.containsKey(nickname))
            return Optional.empty();

        return getContainer(playerData.get(nickname).getStrongbox());
    }

    public synchronized int getPlayerVictoryPoints(String nickname) {
        if (!playerData.containsKey(nickname))
            return 0;

        return playerData.get(nickname).getVictoryPoints();
    }

    /**
     * @param id the ID of the token to be returned
     * @return the token associated with the ID
     */
    public synchronized Optional<ReducedActionToken> getActionToken(int id) {
        return actionTokens.stream().filter(t -> t.getId() == id).findAny();
    }

    /**
     * @param actionTokens the actionTokens to set
     */
    public synchronized void setActionTokens(List<ReducedActionToken> actionTokens) {
        if (actionTokens != null)
            this.actionTokens = new ArrayList<>(actionTokens);
    }

    /**
     * @return blackCross' faith points
     */
    public synchronized int getBlackCrossFP() {
        return blackCrossFP;
    }

    /**
     * @param blackCrossFP the blackCross faith points to set
     */
    public synchronized void setBlackCrossFP(int blackCrossFP) {
        this.blackCrossFP = blackCrossFP;
    }

    /**
     * @param id the id of the container to be returned
     * @return the container corresponding to the id
     */
    public synchronized Optional<ReducedResourceContainer> getContainer(int id) {
        return containers.stream().filter(c -> c.getId() == id).findAny();
    }

    /**
     * @return the containers
     */
    public synchronized List<ReducedResourceContainer> getContainers() {
        return containers;
    }

    /**
     * @param containers the containers to set
     */
    public synchronized void setContainers(List<ReducedResourceContainer> containers) {
        if (containers != null)
            this.containers = new ArrayList<>(containers);
    }

    /**
     * @param container the container to set
     */
    public synchronized void setContainer(ReducedResourceContainer container) {
        containers.replaceAll(c -> c.getId() == container.getId() ? container : c);
    }

    /**
     * @return the currentPlayer
     */
    public synchronized Optional<String> getCurrentPlayer() {
        return Optional.ofNullable(currentPlayer);
    }

    /**
     * @param currentPlayer the currentPlayer to set
     */
    public synchronized void setCurrentPlayer(String currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    /**
     * @return the devCardColors
     */
    public synchronized List<ReducedColor> getDevCardColors() {
        return devCardColors;
    }

    /**
     * @param devCardColors the devCardColors to set
     */
    public synchronized void setDevCardColors(List<ReducedColor> devCardColors) {
        if (devCardColors != null)
            this.devCardColors = new ArrayList<>(devCardColors);
    }

    /**
     * @return the devCardGrid
     */
    public synchronized Optional<ReducedDevCardGrid> getDevCardGrid() {
        return Optional.ofNullable(devCardGrid);
    }

    /**
     * @param devCardGrid the devCardGrid to set
     */
    public synchronized void setDevCardGrid(ReducedDevCardGrid devCardGrid) {
        this.devCardGrid = devCardGrid;
    }

    /**
     * @param id the ID of the card to be returned
     * @return the developmentCard matching the ID
     */
    public synchronized Optional<ReducedDevCard> getDevelopmentCard(int id) {
        return developmentCards.stream().filter(c -> c.getId() == id).findAny();
    }

    /**
     * Returns a development card from the top cards of the development card grid,
     * based on color and level.
     * 
     * @param color the card's color
     * @param level the card's level
     * @return the card of the specified color and level
     */
    public synchronized Optional<ReducedDevCard> getDevCardFromGrid(String color, int level) {
        if (devCardGrid == null ||
            !devCardGrid.getTopCards().keySet().contains(color) ||
            !devCardGrid.getTopCards().get(color).stream()
                .map(id -> getDevelopmentCard(id.orElse(-1)))
                .filter(Optional::isPresent).map(Optional::get)
                .map(ReducedDevCard::getLevel).toList().contains(level))
            
            return Optional.empty();

        return getDevelopmentCard(devCardGrid.getTopCards().get(color).get(level).orElse(-1));
    }

    /**
     * @param developmentCards the developmentCards to set
     */
    public synchronized void setDevelopmentCards(List<ReducedDevCard> developmentCards) {
        if (developmentCards != null)
            this.developmentCards = new ArrayList<>(developmentCards);
    }

    /**
     * @return the faithTrack
     */
    public synchronized Optional<ReducedFaithTrack> getFaithTrack() {
        return Optional.ofNullable(faithTrack);
    }

    /**
     * @param faithTrack the faithTrack to set
     */
    public synchronized void setFaithTrack(ReducedFaithTrack faithTrack) {
        this.faithTrack = faithTrack;
    }

    public synchronized boolean isGameEnded() {
        return isGameEnded;
    }

    /**
     * @return whether it's the last round of the match
     */
    public synchronized boolean isLastRound() {
        return isLastRound;
    }

    /**
     * Sets last round to true.
     */
    public synchronized void setLastRound() {
        this.isLastRound = true;
    }

    public synchronized Optional<ReducedActionToken> getLatestToken() {
        return Optional.ofNullable(latestToken);
    }

    public synchronized void setLatestToken(ReducedActionToken latestToken) {
        this.latestToken = latestToken;
    }

    /**
     * @param id the ID of the card to be returned
     * @return the leaderCard matching the ID
     */
    public synchronized Optional<ReducedLeaderCard> getLeaderCard(int id) {
        return leaderCards.stream().filter(c -> c.getId() == id).findAny();
    }

    /**
     * @param id the ID of the card to be activated
     */
    public synchronized void activateLeaderCard(int id) {
        leaderCards.replaceAll(l -> l.getId() == id ? l.getActivated() : l);
    }

    /**
     * @param leaderCards the leaderCards to set
     */
    public synchronized void setLeaderCards(List<ReducedLeaderCard> leaderCards) {
        if (leaderCards != null)
            this.leaderCards = new ArrayList<>(leaderCards);
    }

    /**
     * @return the market
     */
    public synchronized Optional<ReducedMarket> getMarket() {
        return Optional.ofNullable(market);
    }

    /**
     * @param market the market to set
     */
    public synchronized void setMarket(ReducedMarket market) {
        this.market = market;
    }

    /**
     * @return the playerNicknames
     */
    public synchronized List<String> getPlayerNicknames() {
        return playerNicknames;
    }

    /**
     * @param playerNicknames the playerNicknames to set
     */
    public synchronized void setPlayerNicknames(List<String> playerNicknames) {
        if (this.playerNicknames != null)
            this.playerNicknames = new ArrayList<>(playerNicknames);
            
        for(int i = 0; i < playerNicknames.size(); i++)
            mappedColors.put(playerNicknames.get(i), clientColors.get(i));
    }

    /**
     * @param id the ID of the production to be returned
     * @return the reduced production (transaction recipe)
     */
     public synchronized Optional<ReducedResourceTransactionRecipe> getProduction(int id) {
        return productions.stream().filter(p -> p.getId() == id).findAny();
    }

    /**
     * @param productions the productions to set
     */
    public synchronized void setProductions(List<ReducedResourceTransactionRecipe> productions) {
        if (productions != null)
            this.productions = new ArrayList<>(productions);
    }

    /**
     * @return the resourceTypes
     */
    public synchronized List<ReducedResourceType> getResourceTypes() {
        return resourceTypes;
    }

    /**
     * @param resourceTypes the resourceTypes to set
     */
    public synchronized void setResourceTypes(List<ReducedResourceType> resourceTypes) {
        if (resourceTypes!= null)
            this.resourceTypes = new ArrayList<>(resourceTypes);
    }

    public synchronized int getSlotsCount() {
        return slotsCount;
    }

    public synchronized void setSlotsCount(int slotsCount) {
        this.slotsCount = slotsCount;
    }

    /**
     * @return the vaticanSections
     */
    public synchronized Map<Integer, ReducedVaticanSection> getVaticanSections() {
        return getFaithTrack().isEmpty() ? new HashMap<>() : getFaithTrack().get().getVaticanSections();
    }

    /**
     * @param id the ID of the activated section
     */
    public synchronized void setVaticanSection(int id) {
        if (faithTrack != null)
            faithTrack.getVaticanSections().entrySet().stream()
                .map(Entry::getValue)
                .filter(vs -> vs.getId() == id).findAny().ifPresent(vs -> vs.setActive());
    }

    /**
     * @return the winner
     */
    public synchronized String getWinner() {
        return winner;
    }

    /**
     * @param winner the winner to set
     */
    public synchronized void setWinner(String winner) {
        this.winner = winner;
        this.isGameEnded = true;
    }

    /**
     * @param isSetupDone whether the player setup is done
     */
    public synchronized void setSetupDone(boolean isSetupDone) {
        this.isSetupDone = isSetupDone;
    }

    /**
     * @return whether all the players' setup is done
     */
    public synchronized Optional<Boolean> isSetupDone() {
        return Optional.ofNullable(isSetupDone);
    }

    /**
     * @return the local player's nickname
     */
    public synchronized String getLocalPlayerNickname() {
        return localPlayerNickname;
    }

    /**
     * @param localPlayerNickname the nickname to set
     */
    public synchronized void setLocalPlayerNickname(String localPlayerNickname) {
        this.localPlayerNickname = localPlayerNickname;
    }

    public synchronized String getClientColor(String nick) {
        return mappedColors.get(nick);
    }

    public synchronized boolean isCurrentPlayer() {
        return currentPlayer.equals(localPlayerNickname);
    }
}
