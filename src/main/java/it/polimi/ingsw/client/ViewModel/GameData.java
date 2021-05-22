package it.polimi.ingsw.client.ViewModel;

import java.rmi.NoSuchObjectException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.ingsw.common.reducedmodel.*;

public class GameData {
    private List<ReducedActionToken> actionTokens;
    private int blackCrossFP;
    private List<ReducedResourceContainer> containers;
    private String currentPlayer;
    private List<ReducedColor> devCardColors;
    private ReducedDevCardGrid devCardGrid;
    private List<ReducedDevCard> developmentCards;
    private boolean isLastRound = false;
    private List<ReducedLeaderCard> leaderCards;
    private ReducedMarket market;
    private List<String> playerNicknames;
    private List<ReducedResourceTransactionRecipe> productions;
    private List<ReducedResourceType> resourceTypes;
    private List<Boolean> vaticanSections;
    private String winner;

    public GameData() {
        setVaticanSections(new ArrayList<>());
    }

    /**
     * @return the actionTokens
     */
    public List<ReducedActionToken> getActionTokens() {
        return actionTokens;
    }

    /**
     * @param actionTokens the actionTokens to set
     */
    public void setActionTokens(List<ReducedActionToken> actionTokens) {
        this.actionTokens = actionTokens;
    }

    /**
     * @return the blackCrossFP
     */
    public int getBlackCrossFP() {
        return blackCrossFP;
    }

    /**
     * @param blackCrossFP the blackCrossFP to set
     */
    public void setBlackCrossFP(int blackCrossFP) {
        this.blackCrossFP = blackCrossFP;
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
        this.containers = containers;
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
     * @return the developmentCards
     */
    public List<ReducedDevCard> getDevelopmentCards() {
        return developmentCards;
    }

    /**
     * @param developmentCards the developmentCards to set
     */
    public void setDevelopmentCards(List<ReducedDevCard> developmentCards) {
        this.developmentCards = developmentCards;
    }

    /**
     * @return the isLastRound
     */
    public boolean isLastRound() {
        return isLastRound;
    }

    /**
     * @param isLastRound the isLastRound to set
     */
    public void setLastRound(boolean isLastRound) {
        this.isLastRound = isLastRound;
    }

    /**
     * @param ID the ID of the leader to activate
     * @throws NoSuchObjectException
     */
    public void activateLeader(int ID) throws NoSuchObjectException {
        ReducedLeaderCard l = leaderCards.stream().filter(c -> c.getId() == ID).findAny().orElseThrow(
            () ->  new NoSuchObjectException(String.format("Inexistent leader with ID %d", ID)));
        l.setActive();
    }

    /**
     * Returns the game's reduced leader cards.
     * NOTE: Don't use this to activate a leader card.
     * GameData has an activateLeader(id) method!
     * 
     * @return the leaderCards
     */
    public List<ReducedLeaderCard> getLeaderCards() {
        return leaderCards;
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
     * @return the productions
     */
    public List<ReducedResourceTransactionRecipe> getProductions() {
        return productions;
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
     * @param vaticanSections the vaticanSections to set
     */
    public void setVaticanSections(List<Boolean> vaticanSections) {
        this.vaticanSections = vaticanSections;
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
}
