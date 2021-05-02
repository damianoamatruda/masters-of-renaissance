package it.polimi.ingsw.server.controller.messages;

import it.polimi.ingsw.server.model.DevelopmentCard;
import it.polimi.ingsw.server.model.Production;
import it.polimi.ingsw.server.model.leadercards.LeaderCard;
import it.polimi.ingsw.server.model.resourcecontainers.ResourceContainer;
import it.polimi.ingsw.server.view.View;

import java.util.ArrayList;
import java.util.List;

public class ResGameStarted implements Message {
    private final List<LeaderCard> leaderCards;
    private final List<DevelopmentCard> developmentCards;
    private final List<ResourceContainer> resContainers;
    private final List<Production> productions;
    // TODO: Add the other necessary attributes

    public ResGameStarted(List<LeaderCard> leaderCards, List<DevelopmentCard> developmentCards,
                          List<ResourceContainer> resContainers, List<Production> productions) {
        this.leaderCards = leaderCards;
        this.developmentCards = developmentCards;
        this.resContainers = new ArrayList<>();
        // this.resContainers = resContainers; // TODO: Fix crash during parsing
        this.productions = productions;
    }

    @Override
    public void handle(View view) {
    }

    public List<LeaderCard> getLeaderCards() {
        return leaderCards;
    }

    public List<DevelopmentCard> getDevelopmentCards() {
        return developmentCards;
    }

    public List<ResourceContainer> getResContainers() {
        return resContainers;
    }

    public List<Production> getProductions() {
        return productions;
    }
}
