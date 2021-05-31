package it.polimi.ingsw.client.viewmodel;

import it.polimi.ingsw.common.reducedmodel.*;

import java.util.*;

/** Data storage cache on the Masters Of Renaissance client. */
public class ViewModel {
    /** 
     * Player data.
     * 
     * E.g. ownership of objects, victory points...
     */
    private final Map<String, PlayerData> playerData;


    // UI DATA
    private String localPlayerNickname;
    private boolean isResumedGame;
    private boolean isSetupDone;


    // GAME DATA
    private List<ReducedActionToken> actionTokens;
    private int blackCrossFP;
    private List<ReducedResourceContainer> containers;
    private String currentPlayer;
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
    private final List<Boolean> vaticanSections;
    private String winner;



    /**
     * Class constructor.
     * Initializes empty objects.
     */
    public ViewModel() {
        this.playerData = new HashMap<>();

        vaticanSections = new ArrayList<>();
        isLastRound = false;
        isSetupDone = false;
    }
    
    /**
     * @param nickname the nickname of the player whose data needs to be retrieved.
     * @return the playerData of the specified player
     */
    public PlayerData getPlayerData(String nickname) {
        return playerData.get(nickname);
    }

    public PlayerData getCurrentPlayerData() {
        return getPlayerData(getCurrentPlayer());
    }

    public PlayerData getLocalPlayerData() {
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
     * @return the reduced recipes owned by the player, including:
     *          <ul>
     *              <li>the base production
     *              <li>the development cards productions
     *              <li>the leader cards productions
     *          </ul>
     */
    public List<ReducedResourceTransactionRecipe> getPlayerProductions(String nickname) {
        if (!playerData.containsKey(nickname))
            return null;
        
        PlayerData pd = playerData.get(nickname);
        List<Integer> ids = new ArrayList<>();
        
        ids.add(pd.getBaseProduction());
        getPlayerLeaderCards(nickname).forEach(c -> {
            if (c.getProduction() >= 0)
                ids.add(c.getProduction());
        });
        getPlayerDevelopmentCards(nickname).forEach(c -> {
            if (c.getProduction() >= 0)
                ids.add(c.getProduction());
        });

        return ids.stream()
            .map(id -> getProduction(id).orElse(null))
            .filter(Objects::nonNull)
            .toList();
    }

    /**
     * @param nickname
     * @return the topmost development cards in the player's slots
     */
    private List<ReducedDevCard> getPlayerDevelopmentCards(String nickname) {
        if (!playerData.containsKey(nickname))
            return null;

        return playerData.get(nickname).getDevSlots().stream()
            .map(slot -> getDevelopmentCard(slot.get(0)).orElse(null))
            .filter(Objects::nonNull)
            .toList();
    }

    /**
     * @param nickname
     * @return the reduced leader cards owned by the player
     */
    public List<ReducedLeaderCard> getPlayerLeaderCards(String nickname) {
        if (!playerData.containsKey(nickname))
            return null;

        return playerData.get(nickname).getLeadersHand().stream()
                .map(id -> getLeaderCard(id).orElse(null))
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * @param nickname
     * @return the player's reduced containers, including active leaders' depots
     */
    public List<ReducedResourceContainer> getPlayerShelves(String nickname) {
        if (!playerData.containsKey(nickname))
            return null;
            
        List<Integer> ids = new ArrayList<>();

        playerData.get(nickname).getWarehouseShelves().forEach(id -> ids.add(id));

        getPlayerLeaderCards(nickname).stream()
            .filter(c -> c.isActive())
            .forEach(c -> ids.add(c.getContainerId()));

        return ids.stream()
            .map(id -> getContainer(id).orElse(null))
            .filter(Objects::nonNull)
            .toList();
    }



    // GAME DATA

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
        this.actionTokens = actionTokens;
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
     * @param containers the containers to set
     */
    public void setContainers(List<ReducedResourceContainer> containers) {
        this.containers = new ArrayList<>(containers);
    }

    /**
     * @param container the container to set
     */
    public void setContainer(ReducedResourceContainer container) {
        containers.stream()
            .filter(c -> c.getId() == container.getId())
            .findAny().ifPresent(c -> containers.remove(c));

        containers.add(container);
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
        this.devCardColors = devCardColors;
    }

    /**
     * @return the devCardGrid
     */
    public ReducedDevCardGrid getDevCardGrid() {
        return devCardGrid;
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
     * @param developmentCards the developmentCards to set
     */
    public void setDevelopmentCards(List<ReducedDevCard> developmentCards) {
        this.developmentCards = developmentCards;
    }

    /**
     * @return the faithTrack
     */
    public ReducedFaithTrack getFaithTrack() {
        return faithTrack;
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
     * @param leaderCards the leaderCards to set
     */
    public void setLeaderCards(List<ReducedLeaderCard> leaderCards) {
        this.leaderCards = leaderCards;
    }

    /**
     * @return the market
     */
    public ReducedMarket getMarket() {
        return market;
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
        this.playerNicknames = playerNicknames;
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
        this.productions = productions;
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
        this.resourceTypes = resourceTypes;
    }

    /**
     * @return the vaticanSections
     */
    public List<Boolean> getVaticanSections() {
        return vaticanSections;
    }

    /**
     * @param id the ID of the activated section
     */
    public void setVaticanSection(int id) {
        faithTrack.getVaticanSections().entrySet().stream()
            .map(e -> e.getValue())
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
    }



    // UI DATA
    
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
}
