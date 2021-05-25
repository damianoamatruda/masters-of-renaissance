package it.polimi.ingsw.client.ViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import it.polimi.ingsw.common.reducedmodel.*;

/** The game data sent by the server. */
public class GameData {
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
    private List<Boolean> vaticanSections;
    private String winner;

    public GameData() {
        vaticanSections = new ArrayList<>();
        isLastRound = false;
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
     * @throws NoSuchElementException if the container doesn't exist
     */
    public ReducedResourceContainer getContainer(int id) throws NoSuchElementException {
        return containers.stream().filter(c -> c.getId() == id).findAny().orElseThrow();
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
     * @param id the ID of the card to be returned
     * @return the developmentCard matching the ID
     * @throws NoSuchElementException if the card doesn't exist
     */
    public ReducedDevCard getDevelopmentCard(int id) throws NoSuchElementException{
        return developmentCards.stream().filter(c -> c.getId() == id).findAny().orElseThrow();
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
     * @throws NoSuchElementException if the card doesn't exist
     */
    public ReducedLeaderCard getLeaderCard(int id) throws NoSuchElementException {
        return leaderCards.stream().filter(c -> c.getId() == id).findAny().orElseThrow();
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
}
