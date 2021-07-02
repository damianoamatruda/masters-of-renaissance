package it.polimi.ingsw.common.events.mvevents;

import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.reducedmodel.*;

import java.util.List;

/**
 * Event containing the game's data.
 * Since the Model is completely parameterized,
 * clients need this message to retrieve the server's configuration.
 */
public class UpdateGame extends ViewEvent {
    private final List<ReducedPlayer> players;
    private final List<ReducedDevCardColor> devCardColors;
    private final List<ReducedResourceType> resourceTypes;
    private final List<ReducedLeaderCard> leaderCards;
    private final List<ReducedDevCard> developmentCards;
    private final List<ReducedResourceContainer> resContainers;
    private final List<ReducedResourceTransactionRecipe> productions;
    private final List<ReducedActionToken> actionTokens;
    private final ReducedFaithTrack faithTrack;
    private final ReducedMarket market;
    private final ReducedDevCardGrid devCardGrid;
    private final boolean isSetupDone;
    /** Number of development slots in the player board. */
    private final int devSlotsCount;
    private final String currentPlayer;
    private final String inkwellPlayer;
    private final String winnerPlayer;
    /** Black marker's faith points. */
    private final int blackPoints;
    private final boolean lastRound;
    private final boolean ended;
    /** Whether the current player has already carried out a main action. */
    private final boolean isMandatoryActionDone;

    /**
     * @param view
     * @param players
     * @param devCardColors         development card colors available at play time
     * @param resourceTypes         resource types available at play time
     * @param leaderCards           leader cards available at play time
     * @param developmentCards      development cards available at play time
     * @param resContainers         resource containers available at play time
     * @param productions           productions available at play time
     * @param actionTokens          the game's action tokens (null if multiplayer)
     * @param faithTrack            the game's faith track
     * @param market
     * @param devCardGrid
     * @param devSlotsCount         Number of development slots in the player board
     * @param isSetupDone
     * @param currentPlayer
     * @param inkwellPlayer
     * @param winnerPlayer
     * @param blackPoints           black marker's faith points
     * @param lastRound
     * @param ended
     * @param isMandatoryActionDone Whether the current player has already carried out a main action.
     */
    public UpdateGame(View view,
                      List<ReducedPlayer> players,
                      List<ReducedDevCardColor> devCardColors,
                      List<ReducedResourceType> resourceTypes,
                      List<ReducedLeaderCard> leaderCards,
                      List<ReducedDevCard> developmentCards,
                      List<ReducedResourceContainer> resContainers,
                      List<ReducedResourceTransactionRecipe> productions,
                      List<ReducedActionToken> actionTokens,
                      ReducedFaithTrack faithTrack,
                      ReducedMarket market,
                      ReducedDevCardGrid devCardGrid,
                      int devSlotsCount,
                      boolean isSetupDone,
                      String currentPlayer,
                      String inkwellPlayer,
                      String winnerPlayer,
                      int blackPoints,
                      boolean lastRound,
                      boolean ended,
                      boolean isMandatoryActionDone) {
        super(view);
        this.players = players;
        this.devCardColors = devCardColors;
        this.resourceTypes = resourceTypes;
        this.leaderCards = leaderCards;
        this.developmentCards = developmentCards;
        this.resContainers = resContainers;
        this.productions = productions;
        this.actionTokens = actionTokens;
        this.faithTrack = faithTrack;
        this.market = market;
        this.devCardGrid = devCardGrid;
        this.devSlotsCount = devSlotsCount;
        this.isSetupDone = isSetupDone;
        this.currentPlayer = currentPlayer;
        this.inkwellPlayer = inkwellPlayer;
        this.winnerPlayer = winnerPlayer;
        this.blackPoints = blackPoints;
        this.lastRound = lastRound;
        this.ended = ended;
        this.isMandatoryActionDone = isMandatoryActionDone;
    }

    /**
     * @return the players
     */
    public List<ReducedPlayer> getPlayers() {
        return players;
    }

    /**
     * @return the card colors
     */
    public List<ReducedDevCardColor> getDevCardColors() {
        return devCardColors;
    }

    /**
     * @return the resource types
     */
    public List<ReducedResourceType> getResourceTypes() {
        return resourceTypes;
    }

    /**
     * @return the leader cards
     */
    public List<ReducedLeaderCard> getLeaderCards() {
        return leaderCards;
    }

    /**
     * @return the development cards
     */
    public List<ReducedDevCard> getDevelopmentCards() {
        return developmentCards;
    }

    /**
     * @return the resource containers
     */
    public List<ReducedResourceContainer> getResContainers() {
        return resContainers;
    }

    /**
     * @return the productions
     */
    public List<ReducedResourceTransactionRecipe> getProductions() {
        return productions;
    }

    /**
     * @return the action tokens
     */
    public List<ReducedActionToken> getActionTokens() {
        return actionTokens;
    }

    /**
     * @return <code>true</code> if the game's setup phase is finished
     */
    public boolean isSetupDone() {
        return isSetupDone;
    }

    public ReducedFaithTrack getFaithTrack() {
        return faithTrack;
    }

    public ReducedMarket getMarket() {
        return market;
    }

    public ReducedDevCardGrid getDevCardGrid() {
        return devCardGrid;
    }

    public int getDevSlotsCount() {
        return devSlotsCount;
    }

    public String getCurrentPlayer() {
        return currentPlayer;
    }

    public String getInkwellPlayer() {
        return inkwellPlayer;
    }

    public String getWinnerPlayer() {
        return winnerPlayer;
    }

    public int getBlackPoints() {
        return blackPoints;
    }

    public boolean isLastRound() {
        return lastRound;
    }

    public boolean isEnded() {
        return ended;
    }

    public boolean isMandatoryActionDone() {
        return isMandatoryActionDone;
    }
}
