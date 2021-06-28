package it.polimi.ingsw.common.events.mvevents;

import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.reducedmodel.*;

import java.util.List;

public class UpdateGame extends ViewEvent {
    private final List<ReducedColor> colors;
    private final List<ReducedResourceType> resourceTypes;
    private final List<String> players;
    private final List<ReducedLeaderCard> leaderCards;
    private final List<ReducedDevCard> developmentCards;
    private final List<ReducedResourceContainer> resContainers;
    private final List<ReducedResourceTransactionRecipe> productions;
    private final List<ReducedActionToken> actionTokens;
    private final ReducedFaithTrack faithTrack;
    private final boolean isSetupDone;
    private final int slotsCount;
    private final String inkwellPlayer;

    /**
     * Class constructor.
     *
     * @param view
     * @param players
     * @param colors           development card colors available at play time
     * @param resourceTypes    resource types available at play time
     * @param leaderCards      leader cards available at play time
     * @param developmentCards development cards available at play time
     * @param resContainers    resource containers available at play time
     * @param productions      productions available at play time
     * @param faithTrack       the game's faith track
     * @param actionTokens     the game's action tokens (null if multiplayer)
     */
    public UpdateGame(View view,
                      List<String> players,
                      List<ReducedColor> colors,
                      List<ReducedResourceType> resourceTypes,
                      List<ReducedLeaderCard> leaderCards,
                      List<ReducedDevCard> developmentCards,
                      List<ReducedResourceContainer> resContainers,
                      List<ReducedResourceTransactionRecipe> productions,
                      ReducedFaithTrack faithTrack,
                      int slotsCount,
                      List<ReducedActionToken> actionTokens,
                      boolean isSetupDone,
                      String inkwellPlayer) {
        super(view);
        this.players = players;
        this.leaderCards = leaderCards;
        this.developmentCards = developmentCards;
        this.resContainers = resContainers;
        this.productions = productions;
        this.actionTokens = actionTokens;
        this.colors = colors;
        this.resourceTypes = resourceTypes;
        this.faithTrack = faithTrack;
        this.isSetupDone = isSetupDone;
        this.slotsCount = slotsCount;
        this.inkwellPlayer = inkwellPlayer;
    }

    public UpdateGame(List<String> players,
                      List<ReducedColor> colors,
                      List<ReducedResourceType> resourceTypes,
                      List<ReducedLeaderCard> leaderCards,
                      List<ReducedDevCard> developmentCards,
                      List<ReducedResourceContainer> resContainers,
                      List<ReducedResourceTransactionRecipe> productions,
                      ReducedFaithTrack faithTrack,
                      int slotsCount,
                      List<ReducedActionToken> actionTokens,
                      boolean isSetupDone,
                      String inkwellPlayer) {
        this(null, players, colors, resourceTypes, leaderCards, developmentCards, resContainers, productions, faithTrack, slotsCount, actionTokens, isSetupDone, inkwellPlayer);
    }

    /**
     * @return the players' nicknames
     */
    public List<String> getPlayers() {
        return players;
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
     * @return the card colors
     */
    public List<ReducedColor> getColors() {
        return colors;
    }

    /**
     * @return the resource types
     */
    public List<ReducedResourceType> getResourceTypes() {
        return resourceTypes;
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

    public int getSlotsCount() {
        return slotsCount;
    }

    public String getInkwellPlayer() {
        return inkwellPlayer;
    }
}
