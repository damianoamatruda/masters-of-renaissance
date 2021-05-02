package it.polimi.ingsw.server.model.gamecontext;

import it.polimi.ingsw.server.model.*;
import it.polimi.ingsw.server.model.cardrequirements.CardRequirementsNotMetException;
import it.polimi.ingsw.server.model.resourcecontainers.ResourceContainer;
import it.polimi.ingsw.server.model.resourcetypes.ResourceType;

import java.util.Map;

/**
 * This class represents the state of a game during the turn of a player that has not made the mandatory move yet.
 */
public class GameTurnNotDoneState extends GameTurnState {
    @Override
    public void takeMarketResources(GameContext context, Player player, boolean isRow, int index,
                                    Map<ResourceType, Integer> replacements,
                                    Map<ResourceContainer, Map<ResourceType, Integer>> shelves) throws IllegalActionException, IllegalMarketTransferException {
        checkCurrentPlayer(context, player);
        context.game.getMarket().takeResources(context.game, player, isRow, index, replacements, shelves);
        context.setState(new GameTurnDoneState());
    }

    @Override
    public void buyDevCard(GameContext context, Player player, DevCardColor color, int level, int position,
                           Map<ResourceContainer, Map<ResourceType, Integer>> resContainers) throws IllegalActionException, CardRequirementsNotMetException, IllegalCardDepositException {
        checkCurrentPlayer(context, player);
        context.game.getDevCardGrid().buyDevCard(context.game, player, color, level, position, resContainers);
        context.setState(new GameTurnDoneState());
    }

    @Override
    public void activateProductionGroup(GameContext context, Player player, ProductionGroup productionGroup) throws IllegalActionException, IllegalProductionActivationException {
        checkCurrentPlayer(context, player);
        productionGroup.activate(context.game, player);
        context.setState(new GameTurnDoneState());
    }
}
