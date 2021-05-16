package it.polimi.ingsw.common.backend.model;

import it.polimi.ingsw.common.backend.model.actiontokens.ActionToken;
import it.polimi.ingsw.common.backend.model.leadercards.LeaderCard;
import it.polimi.ingsw.common.backend.model.resourcecontainers.ResourceContainer;
import it.polimi.ingsw.common.backend.model.resourcetransactions.ResourceTransactionRecipe;
import it.polimi.ingsw.common.events.mvevents.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class represents a single-player game of Masters of Renaissance. It contains all the extra functionality.
 */
public class SoloGame extends Game {
    /** The deck of action tokens to be activated at the end of the player's turn. */
    protected final List<ActionToken> actionTokens;

    /** The "marker" of Lorenzo il Magnifico on the faith track. */
    protected int blackPoints;

    /** Flag that determines whether Lorenzo has won the game. */
    protected boolean blackWinner;

    /**
     * Initializes the solo game with the following parameters.
     *
     * @param player                the nickname of the player who joined
     * @param leaderCards           the list of leader cards
     * @param developmentCards      the list of development cards
     * @param resContainers         the list of resource containers
     * @param productions           the list of productions
     * @param devCardGrid           the development card grid
     * @param market                the resource market
     * @param faithTrack            the faith track
     * @param actionTokens          the deck of tokens, of which the top token is activated after each round
     * @param maxFaithPointsCount   the number of the last reachable faith track tile by a player
     * @param maxObtainableDevCards the number of development cards a player can have, before triggering the end of the
     */
    public SoloGame(Player player, List<LeaderCard> leaderCards, List<DevelopmentCard> developmentCards,
                    List<ResourceContainer> resContainers, List<ResourceTransactionRecipe> productions,
                    DevCardGrid devCardGrid, Market market, FaithTrack faithTrack, List<ActionToken> actionTokens,
                    int maxFaithPointsCount, int maxObtainableDevCards) {
        super(List.of(player), leaderCards, developmentCards, resContainers, productions, devCardGrid, market, faithTrack, maxFaithPointsCount, maxObtainableDevCards);
        this.actionTokens = new ArrayList<>(actionTokens);
        this.blackPoints = 0;
        this.blackWinner = false;
    }

    @Override
    public void emitInitialState() {
        emit(new UpdateGameStart(
                players.stream().map(Player::getNickname).toList(),
                leaderCards.stream().map(LeaderCard::reduce).toList(),
                developmentCards.stream().map(DevelopmentCard::reduce).toList(),
                resContainers.stream()
                        .collect(Collectors.toMap(
                                ResourceContainer::reduce,
                                c -> players.stream()
                                        .filter(pl -> pl.getResourceContainerById(c.getId()).isPresent())
                                        .findAny().get().getNickname())),
                productions.stream().map(ResourceTransactionRecipe::reduce).toList()));
        // p.getBaseProduction().getId(), // FileGameFactory.baseProduction is unique, so same ID returned in all calls
        // actionTokens.stream().map(ActionToken::reduce).toList(),
        // p.getLeaders().stream().map(Card::getId).toList(),
        // p.getWarehouse().getShelves().stream().map(ResourceContainer::getId).toList(),
        // p.getStrongbox().getId(),
        // p.getSetup().reduce()));

        emit(new UpdateCurrentPlayer(getCurrentPlayer().getNickname()));
    }

    @Override
    public void emitResumeState() {
        emit(new UpdateGameResume(
                players.stream().map(Player::getNickname).toList(),
                leaderCards.stream().map(LeaderCard::reduce).toList(),
                developmentCards.stream().map(DevelopmentCard::reduce).toList(),
                resContainers.stream()
                        .collect(Collectors.toMap(
                                ResourceContainer::reduce,
                                c -> players.stream()
                                        .filter(pl -> pl.getResourceContainerById(c.getId()).isPresent())
                                        .findAny().get().getNickname())),
                productions.stream().map(ResourceTransactionRecipe::reduce).toList()));
        // p.getBaseProduction().getId(), // FileGameFactory.baseProduction is unique, so same ID returned in all calls
        // actionTokens.stream().map(ActionToken::reduce).toList(),
        // p.getLeaders().stream().map(Card::getId).toList(),
        // p.getWarehouse().getShelves().stream().map(ResourceContainer::getId).toList(),
        // p.getStrongbox().getId(),
        // p.getSetup().reduce()));

        emit(new UpdateCurrentPlayer(getCurrentPlayer().getNickname()));
    }

    @Override
    public void onDiscardResources(Player player, int quantity) {
        incrementBlackPoints(quantity);
    }

    /**
     * Triggered after the player concludes a turn. This is Lorenzo's turn: a token will be activated.
     *
     * @throws NoActivePlayersException all players are inactive
     */
    @Override
    public void onTurnEnd() throws NoActivePlayersException {
        ActionToken token = actionTokens.remove(0);
        token.trigger(this);
        actionTokens.add(token);

        emit(new UpdateActionToken(token.getId()));

        /* Check if Lorenzo is winning */
        if (blackPoints == maxFaithPointsCount || devCardGrid.numOfAvailableColors() < devCardGrid.getColorsCount())
            blackWinner = true;

        if (!blackWinner)
            super.onTurnEnd();
        else {
            ended = true;
            emit(new UpdateGameEnd(null));
        }
    }

    @Override
    public int getBlackPoints() {
        return blackPoints;
    }

    @Override
    public boolean isBlackWinner() {
        return blackWinner;
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

        blackPoints += points;

        onIncrementFaithPoints(blackPoints);

        emit(new UpdateFaithPoints(null, blackPoints, true));
    }
}
