package it.polimi.ingsw.server.model.gamecontext;

import it.polimi.ingsw.server.model.CannotChooseException;
import it.polimi.ingsw.server.model.InvalidChoiceException;
import it.polimi.ingsw.server.model.Market;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.leadercards.LeaderCard;
import it.polimi.ingsw.server.model.resourcecontainers.IllegalResourceTransferException;
import it.polimi.ingsw.server.model.resourcecontainers.ResourceContainer;
import it.polimi.ingsw.server.model.resourcetypes.ResourceType;

import java.util.List;
import java.util.Map;

/**
 * This class represents the setup state of a game.
 */
public class GameSetupState extends GameState {
    @Override
    public Market getMarket(GameContext context) {
        return context.game.getMarket();
    }

    @Override
    public void chooseLeaders(GameContext context, Player player, List<LeaderCard> leaders) throws CannotChooseException {
        player.chooseLeaders(leaders);
        checkEndSetup(context);
    }

    @Override
    public void chooseResources(GameContext context, Player player, Map<ResourceType, Integer> chosenResources,
                                Map<ResourceContainer, Map<ResourceType, Integer>> shelves) throws IllegalResourceTransferException, CannotChooseException, InvalidChoiceException {
        player.chooseResources(context.game, chosenResources, shelves);
        checkEndSetup(context);
    }

    /**
     * Check if the last necessary setup move has been made.
     *
     * @param context the context
     */
    private void checkEndSetup(GameContext context) {
        if (context.game.getPlayers().stream().allMatch(p -> p.hasChosenLeaders() && p.hasChosenResources()))
            context.setState(new GameTurnNotDoneState());
    }
}
