package it.polimi.ingsw.server.controller.messages;

import it.polimi.ingsw.server.model.DevCardGrid;
import it.polimi.ingsw.server.model.DevelopmentCard;
import it.polimi.ingsw.server.model.Market;
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
    private final Market market;
    private final DevCardGrid devCardGrid;

    public ResGameStarted(List<LeaderCard> leaderCards, List<DevelopmentCard> developmentCards,
                          List<ResourceContainer> resContainers, List<Production> productions, Market market,
                          DevCardGrid devCardGrid) {
        this.leaderCards = leaderCards;
        this.developmentCards = developmentCards;
        this.resContainers = new ArrayList<>();
        // this.resContainers = resContainers; // TODO: Fix crash during parsing
        this.productions = productions;
        this.market = market;
        this.devCardGrid = devCardGrid;
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

    public Market getMarket() {
        return market;
    }

    public DevCardGrid getDevCardGrid() {
        return devCardGrid;
    }
}
