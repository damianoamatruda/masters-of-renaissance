package it.polimi.ingsw.common.events.mvevents;

import it.polimi.ingsw.common.reducedmodel.*;

import java.util.List;

public class UpdateGameStart implements MVEvent {
    // TODO commproto docs
    private final List<String> players;
    private final List<ReducedLeaderCard> leaderCards;
    private final List<ReducedDevCard> developmentCards;
    private final List<ReducedResourceContainer> resContainers;
    private final List<ReducedResourceTransactionRecipe> productions;
    private final List<ReducedActionToken> actionTokens;

    /**
     * Class constructor.
     *
     * @param leaderCards      leader cards available at play time
     * @param developmentCards development cards available at play time
     * @param resContainers    resource containers available at play time
     * @param productions      productions available at play time
     */
    public UpdateGameStart(List<String> players,
                           List<ReducedLeaderCard> leaderCards,
                           List<ReducedDevCard> developmentCards,
                           List<ReducedResourceContainer> resContainers,
                           List<ReducedResourceTransactionRecipe> productions,
                           List<ReducedActionToken> actionTokens) {
        this.players = players;
        this.leaderCards = leaderCards;
        this.developmentCards = developmentCards;
        this.resContainers = resContainers;
        this.productions = productions;
        this.actionTokens = actionTokens;
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
}
