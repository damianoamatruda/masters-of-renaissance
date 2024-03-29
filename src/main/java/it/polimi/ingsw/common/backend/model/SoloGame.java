package it.polimi.ingsw.common.backend.model;

import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.backend.model.actiontokens.ActionToken;
import it.polimi.ingsw.common.backend.model.leadercards.LeaderCard;
import it.polimi.ingsw.common.backend.model.resourcecontainers.ResourceContainer;
import it.polimi.ingsw.common.backend.model.resourcetransactions.ResourceTransactionRecipe;
import it.polimi.ingsw.common.backend.model.resourcetypes.ResourceType;
import it.polimi.ingsw.common.events.mvevents.UpdateActionToken;
import it.polimi.ingsw.common.events.mvevents.UpdateFaithPoints;
import it.polimi.ingsw.common.events.mvevents.UpdateGame;
import it.polimi.ingsw.common.events.mvevents.UpdateGameEnd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class represents a single-player game of Masters of Renaissance. It contains all the extra functionality.
 */
public class SoloGame extends Game {
    /** The deck of action tokens to be activated at the end of the player's turn. */
    protected final List<ActionToken> actionTokens;

    /** Lorenzo il Magnifico's marker on the faith track. */
    protected int blackPoints;

    /** Flag that determines whether Lorenzo has won the game. */
    protected boolean blackWinner;

    /**
     * Initializes the solo game with the following parameters.
     *
     * @param player                the nickname of the player who joined
     * @param colors                the list of development card colors
     * @param resourceTypes         the list of resource types
     * @param leaderCards           the list of leader cards
     * @param developmentCards      the list of development cards
     * @param resContainers         the list of resource containers
     * @param productions           the list of productions
     * @param actionTokens          the deck of tokens, of which the top token is activated after each round
     * @param devCardGrid           the development card grid
     * @param market                the resource market
     * @param faithTrack            the faith track
     * @param maxObtainableDevCards the number of development cards a player can have, before triggering the end of the
     */
    public SoloGame(Player player, List<DevCardColor> colors, List<ResourceType> resourceTypes, List<LeaderCard> leaderCards, List<DevelopmentCard> developmentCards,
                    List<ResourceContainer> resContainers, List<ResourceTransactionRecipe> productions,
                    List<ActionToken> actionTokens, DevCardGrid devCardGrid, Market market, FaithTrack faithTrack,
                    int maxObtainableDevCards, int devSlotsCount) {
        super(List.of(player), colors, resourceTypes, leaderCards, developmentCards, resContainers, productions, devCardGrid, market, faithTrack, maxObtainableDevCards, devSlotsCount);
        this.actionTokens = new ArrayList<>(actionTokens);
        this.blackPoints = 0;
        this.blackWinner = false;
    }

    @Override
    public void dispatchState(View view, Player player, boolean isMandatoryActionDone) {
        dispatch(new UpdateGame(
                view,
                players.stream().map(p -> p.reduce(p.equals(player))).toList(),
                devCardColors.stream().map(DevCardColor::reduce).toList(),
                resourceTypes.stream().map(ResourceType::reduce).toList(),
                leaderCards.stream().map(LeaderCard::reduce).toList(),
                developmentCards.stream().map(DevelopmentCard::reduce).toList(),
                resContainers.stream().map(ResourceContainer::reduce).toList(),
                productions.stream().map(ResourceTransactionRecipe::reduce).toList(),
                actionTokens.stream().map(ActionToken::reduce).toList(),
                faithTrack.reduce(),
                market.reduce(),
                devCardGrid.reduce(),
                devSlotsCount,
                players.stream().map(Player::getSetup).allMatch(PlayerSetup::isDone),
                getInkwellPlayer().getNickname(),
                getCurrentPlayer().getNickname(),
                getWinnerPlayer().map(Player::getNickname).orElse(null),
                blackPoints,
                lastRound,
                ended,
                isMandatoryActionDone));
    }

    @Override
    public void onDiscardResources(Player player, int quantity) {
        incrementBlackPoints(quantity);
    }

    /**
     * Triggered after the player concludes a turn. This is Lorenzo's turn: a token will be activated.
     */
    @Override
    public void onTurnEnd() {
        ActionToken token = actionTokens.remove(0);
        token.trigger(this);
        actionTokens.add(token);

        activateVaticanSections();

        dispatch(new UpdateActionToken(token.getId()));

        /* Check if Lorenzo is winning */
        if (blackPoints >= faithTrack.getMaxFaithPointsCount() || devCardGrid.numOfAvailableColors() < devCardGrid.getColorsCount())
            blackWinner = true;

        if (!blackWinner)
            super.onTurnEnd();
        else {
            ended = true;
            dispatch(new UpdateGameEnd(null));
        }
    }

    @Override
    public List<ActionToken> getActionTokens() {
        return List.copyOf(actionTokens);
    }

    @Override
    public int getBlackPoints() {
        return blackPoints;
    }

    @Override
    public boolean isBlackWinner() {
        return blackWinner;
    }

    @Override
    public int getMaxFaithPoints() {
        return Math.max(super.getMaxFaithPoints(), blackPoints);
    }

    /**
     * This action is triggered by certain type(s) of token. Shuffles and resets the deck.
     */
    public void shuffleActionTokens() {
        Collections.shuffle(actionTokens);
    }

    /**
     * Advances Lorenzo's marker on the faith track by one, then checks for Vatican Report.
     *
     * @param points the quantity to be added to the black points
     */
    public void incrementBlackPoints(int points) {
        if (points <= 0)
            return;

        blackPoints = Integer.min(blackPoints + points, getFaithTrack().getMaxFaithPointsCount());

        dispatch(new UpdateFaithPoints(null, blackPoints, true));
    }
}
