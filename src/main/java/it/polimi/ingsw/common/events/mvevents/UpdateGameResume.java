package it.polimi.ingsw.common.events.mvevents;

import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.reducedmodel.*;

import java.util.List;

public class UpdateGameResume extends ViewEvent {
    // TODO commproto docs
    private final List<String> players;
    private final List<ReducedLeaderCard> leaderCards;
    private final List<ReducedDevCard> developmentCards;
    private final List<ReducedResourceContainer> resContainers;
    private final List<ReducedResourceTransactionRecipe> productions;
    private final List<ReducedActionToken> actionTokens;
    private final List<ReducedColor> colors;
    private final List<ReducedResourceType> resourceTypes;

    /**
     * Class constructor.
     *
     * @param view
     * @param leaderCards      leader cards available at play time
     * @param developmentCards development cards available at play time
     * @param resContainers    resource containers available at play time
     * @param productions      productions available at play time
     * @param resourceTypes    resource types available at play time
     * @param colors           development card colors available at play time
     */
    public UpdateGameResume(View view,
                            List<String> players,
                            List<ReducedLeaderCard> leaderCards,
                            List<ReducedDevCard> developmentCards,
                            List<ReducedResourceContainer> resContainers,
                            List<ReducedResourceTransactionRecipe> productions,
                            List<ReducedResourceType> resourceTypes, List<ReducedColor> colors, List<ReducedActionToken> actionTokens) {
        super(view);
        this.players = players;
        this.leaderCards = leaderCards;
        this.developmentCards = developmentCards;
        this.resContainers = resContainers;
        this.productions = productions;
        this.actionTokens = actionTokens;
        this.colors = colors;
        this.resourceTypes = resourceTypes;
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
}
