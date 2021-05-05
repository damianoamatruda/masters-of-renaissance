package it.polimi.ingsw.common.events;

import it.polimi.ingsw.common.ModelObserver;

public class ResGameStarted implements MVEvent {
    @Override
    public void handle(ModelObserver view) {
        view.update(this);
    }

    // TODO
    /* private final List<LeaderCard> leaderCards;
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
    } */
}
