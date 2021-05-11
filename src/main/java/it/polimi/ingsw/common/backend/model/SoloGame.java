package it.polimi.ingsw.common.backend.model;

import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.backend.model.actiontokens.ActionToken;
import it.polimi.ingsw.common.backend.model.leadercards.LeaderCard;
import it.polimi.ingsw.common.backend.model.resourcecontainers.ResourceContainer;
import it.polimi.ingsw.common.backend.model.resourcetransactions.ResourceTransactionRecipe;
import it.polimi.ingsw.common.events.UpdateActionToken;
import it.polimi.ingsw.common.events.UpdateFaithTrack;
import it.polimi.ingsw.common.events.UpdateGameStart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    public void addObserver(View o, String nickname) {
        this.observers.add(o);
        this.players.forEach(obj -> obj.addObserver(o));
        this.leaderCards.forEach(obj -> obj.addObserver(o));
        // this.developmentCards.forEach(obj -> obj.addObserver(o)); // technically unneded
        this.resContainers.forEach(obj -> obj.addObserver(o));
        this.devCardGrid.addObserver(o);
        this.market.addObserver(o);
        this.faithTrack.addObserver(o);

        Player p = players.stream().filter(pl -> pl.getNickname().equals(nickname)).findFirst().orElseThrow();

        List<Integer> leaders = p.getLeaders().stream().map(Card::getId).toList();
        List<Integer> shelves = p.getWarehouse().getShelves().stream().map(ResourceContainer::getId).toList();
        int strongbox = p.getStrongbox().getId();

        notify(o, new UpdateGameStart(
            players.stream().map(pl -> pl.getNickname()).toList(),
            market.reduce(),
            devCardGrid.reduce(),
            leaderCards,
            developmentCards,
            resContainers.stream().map(c -> c.reduce()).toList(),
            productions,
            leaders,
            shelves,
            strongbox,
            actionTokens));
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

        notifyBroadcast(new UpdateActionToken(token.getId(), actionTokens.stream().map(ActionToken::getId).toList()));

        checkBlackWin();

        if (!isBlackWinner())
            super.onTurnEnd();
        else ended = true;
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
     * Checks if Lorenzo is winning.
     */
    private void checkBlackWin() {
        if (blackPoints == maxFaithPointsCount || devCardGrid.numOfAvailableColors() < devCardGrid.getColorsCount()) {
            setBlackWinner();
        }
    }

    /**
     * This action is triggered by certain type(s) of token. Shuffles and resets the deck.
     */
    public void shuffleActionTokens() {
        Collections.shuffle(actionTokens);
    }

    /**
     * Advances Lorenzo's marker on the faith track by one, then checks for Vatican Report.
     */
    public void incrementBlackPoints() {
        blackPoints += 1;
        super.onIncrementFaithPoints(blackPoints);

        notifyBroadcast(new UpdateFaithTrack(null, blackPoints, true));
    }

    /**
     * Declares Lorenzo as winner.
     */
    public void setBlackWinner() {
        this.blackWinner = true; // TODO notify
    }
}
