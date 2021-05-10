package it.polimi.ingsw.common.events;

import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.reducedmodel.ReducedDevCardGrid;
import it.polimi.ingsw.common.reducedmodel.ReducedMarket;
import it.polimi.ingsw.server.model.DevelopmentCard;
import it.polimi.ingsw.server.model.leadercards.LeaderCard;
import it.polimi.ingsw.server.model.resourcecontainers.ResourceContainer;
import it.polimi.ingsw.server.model.resourcetransactions.ResourceTransactionRecipe;

import java.util.List;

public class ResGameStarted implements MVEvent {
    private final ReducedMarket market;
    private final ReducedDevCardGrid developmentCardGrid;
    List<LeaderCard> leaderCards;
    List<DevelopmentCard> developmentCards;
    List<ResourceContainer> resContainers;
    List<ResourceTransactionRecipe> productions;

    public ResGameStarted(
            ReducedMarket market,
            ReducedDevCardGrid developmentCardGrid,
            List<LeaderCard> leaderCards,
            List<DevelopmentCard> developmentCards,
            List<ResourceContainer> resContainers,
            List<ResourceTransactionRecipe> productions) {
        this.market = market;
        this.developmentCardGrid = developmentCardGrid;
        this.leaderCards = leaderCards;
        this.developmentCards = developmentCards;
        this.resContainers = resContainers;
        this.productions = productions;
    }

    /**
     * @return the reduced development card grid
     */
    public ReducedDevCardGrid getDevelopmentCardGrid() {
        return developmentCardGrid;
    }

    /**
     * @return the reduced market
     */
    public ReducedMarket getMarket() {
        return market;
    }

    @Override
    public void handle(View view) {
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
