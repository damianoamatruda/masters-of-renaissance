package it.polimi.ingsw;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.actiontokens.ActionToken;
import it.polimi.ingsw.model.actiontokens.ActionTokenBlackMoveOneShuffle;
import it.polimi.ingsw.model.actiontokens.ActionTokenBlackMoveTwo;
import it.polimi.ingsw.model.actiontokens.ActionTokenDiscardTwo;
import it.polimi.ingsw.model.cardrequirements.DevCardRequirement;
import it.polimi.ingsw.model.cardrequirements.ResourceRequirement;
import it.polimi.ingsw.model.devcardcolors.Blue;
import it.polimi.ingsw.model.devcardcolors.Green;
import it.polimi.ingsw.model.devcardcolors.Purple;
import it.polimi.ingsw.model.devcardcolors.Yellow;
import it.polimi.ingsw.model.leadercards.*;
import it.polimi.ingsw.model.resourcecontainers.Strongbox;
import it.polimi.ingsw.model.resourcecontainers.Warehouse;
import it.polimi.ingsw.model.resourcetypes.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

/**
 * This class builds games with the original parameters.
 */
public class JavaGameFactory implements GameFactory {
    /** Maximum number of players that can connect to the same game instance. */
    private final int maxPlayersCount = 4;

    /** Number of distinct leader cards given to each player at the beginning of the game. */
    private final int playerLeadersCount = 4;

    /**
     * Builder of an original multiplayer game.
     *
     * @param nicknames the list of nicknames of players who joined
     * @return          the multiplayer game
     */
    public Game buildMultiGame(List<String> nicknames) {
        if (nicknames.size() > maxPlayersCount)
            throw new RuntimeException();

        /* Shuffle the leader cards */
        List<LeaderCard> leaderCards = new ArrayList<>(generateLeaderCards());
        Collections.shuffle(leaderCards);
        if (playerLeadersCount > 0 && leaderCards.size() % playerLeadersCount != 0)
            throw new RuntimeException();

        /* Shuffle the nicknames */
        List<String> shuffledNicknames = new ArrayList<>(nicknames);
        Collections.shuffle(shuffledNicknames);
        if (playerLeadersCount > 0 && shuffledNicknames.size() > leaderCards.size() / playerLeadersCount)
            throw new RuntimeException();

        List<Player> players = new ArrayList<>();
        for (int i = 0; i < shuffledNicknames.size(); i++) {
            Player player = new Player(
                    shuffledNicknames.get(i),
                    i == 0,
                    leaderCards.subList(playerLeadersCount * i, playerLeadersCount * (i+1)),
                    new Warehouse(3),
                    new Strongbox(),
                    new Production(Map.of(), 2, Map.of(), 1), 3
            );
            players.add(player);
        }

        return new Game(
                players,
                new DevCardGrid(generateDevCards(), 3, 4),
                new Market(generateMarketResources(), 4),
                generateVaticanSections(), generateYellowTiles(), 24,
                7
        );
    }

    /**
     * Builder of an original single-player game.
     *
     * @param nickname  the nickname of the only player
     * @return          the single-player game
     */
    public SoloGame buildSoloGame(String nickname) {
        /* Shuffle the leader cards */
        List<LeaderCard> shuffledLeaderCards = new ArrayList<>(generateLeaderCards());
        Collections.shuffle(shuffledLeaderCards);

        Player player = new Player(
                nickname,
                true,
                shuffledLeaderCards.subList(0, playerLeadersCount),
                new Warehouse(3),
                new Strongbox(),
                new Production(Map.of(), 2, Map.of(), 1), 3
        );

        return new SoloGame(
                player,
                new DevCardGrid(generateDevCards(), 3, 4),
                new Market(generateMarketResources(), 4),
                generateVaticanSections(), generateYellowTiles(), generateActionTokens(), 24,
                7);
    }

    /**
     * Getter of the maximum number of players that can connect to a game.
     *
     * @return the maximum number of players
     */
    public int getMaxPlayersCount() {
        return maxPlayersCount;
    }

    /**
     * Getter of the number of distinct leader cards given to each player at the beginning of the game.
     *
     * @return the number of leader cards
     */
    public int getPlayerLeadersCount() {
        return playerLeadersCount;
    }

    /**
     * Returns a list of all possible development cards.
     *
     * @return  list of development cards
     */
    public List<DevelopmentCard> generateDevCards() {
        return List.of(
                /* 1 */
                new DevelopmentCard(
                        Green.getInstance(), 1,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Shield.getInstance(), 2))),
                        new Production(
                                Map.ofEntries(
                                        entry(Coin.getInstance(), 1)),
                                0, Map.ofEntries(
                                        entry(Faith.getInstance(), 1)), 0),
                        1),
                /* 2 */
                new DevelopmentCard(
                        Purple.getInstance(), 1,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Servant.getInstance(), 2))),
                        new Production(
                                Map.ofEntries(
                                        entry(Stone.getInstance(), 1)),
                                0, Map.ofEntries(
                                        entry(Faith.getInstance(), 1)), 0),
                        1),
                /* 3 */
                new DevelopmentCard(
                        Blue.getInstance(), 1,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Coin.getInstance(), 2))),
                        new Production(
                                Map.ofEntries(
                                        entry(Shield.getInstance(), 1)),
                                0, Map.ofEntries(
                                        entry(Faith.getInstance(), 1)), 0),
                        1),
                /* 4 */
                new DevelopmentCard(
                        Yellow.getInstance(), 1,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Stone.getInstance(), 2))),
                        new Production(
                                Map.ofEntries(
                                        entry(Servant.getInstance(), 1)),
                                0, Map.ofEntries(
                                        entry(Faith.getInstance(), 1)), 0),
                        1),
                /* 5 */
                new DevelopmentCard(
                        Green.getInstance(), 1,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Shield.getInstance(), 1),
                                entry(Servant.getInstance(), 1),
                                entry(Stone.getInstance(), 1))),
                        new Production(
                                Map.ofEntries(
                                        entry(Stone.getInstance(), 1)),
                                0, Map.ofEntries(
                                        entry(Servant.getInstance(), 1)), 0),
                        2),
                /* 6 */
                new DevelopmentCard(
                        Purple.getInstance(), 1,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Shield.getInstance(), 1),
                                entry(Servant.getInstance(), 1),
                                entry(Coin.getInstance(), 1))),
                        new Production(
                                Map.ofEntries(
                                        entry(Coin.getInstance(), 1)),
                                0, Map.ofEntries(
                                        entry(Shield.getInstance(), 1)), 0),
                        2),
                /* 7 */
                new DevelopmentCard(
                        Blue.getInstance(), 1,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Coin.getInstance(), 1),
                                entry(Servant.getInstance(), 1),
                                entry(Stone.getInstance(), 1))),
                        new Production(
                                Map.ofEntries(
                                        entry(Servant.getInstance(), 1)),
                                0, Map.ofEntries(
                                        entry(Stone.getInstance(), 1)), 0),
                        2),
                /* 8 */
                new DevelopmentCard(
                        Yellow.getInstance(), 1,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Shield.getInstance(), 1),
                                entry(Stone.getInstance(), 1),
                                entry(Coin.getInstance(), 1))),
                        new Production(
                                Map.ofEntries(
                                        entry(Shield.getInstance(), 1)),
                                0, Map.ofEntries(
                                        entry(Coin.getInstance(), 1)), 0),
                        2),
                /* 9 */
                new DevelopmentCard(
                        Green.getInstance(), 1,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Shield.getInstance(), 3))),
                        new Production(
                                Map.ofEntries(
                                        entry(Servant.getInstance(), 2)),
                                0, Map.ofEntries(
                                        entry(Coin.getInstance(), 1),
                                        entry(Shield.getInstance(), 1),
                                        entry(Stone.getInstance(), 1)), 0),
                        3),
                /* 10 */
                new DevelopmentCard(
                        Purple.getInstance(), 1,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Servant.getInstance(), 3))),
                        new Production(
                                Map.ofEntries(
                                        entry(Coin.getInstance(), 2)),
                                0, Map.ofEntries(
                                        entry(Servant.getInstance(), 1),
                                        entry(Shield.getInstance(), 1),
                                        entry(Stone.getInstance(), 1)), 0),
                        3),
                /* 11 */
                new DevelopmentCard(
                        Blue.getInstance(), 1,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Coin.getInstance(), 3))),
                        new Production(
                                Map.ofEntries(
                                        entry(Stone.getInstance(), 2)),
                                0, Map.ofEntries(
                                        entry(Coin.getInstance(), 1),
                                        entry(Servant.getInstance(), 1),
                                        entry(Stone.getInstance(), 1)), 0),
                        3),
                /* 12 */
                new DevelopmentCard(
                        Yellow.getInstance(), 1,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Stone.getInstance(), 3))),
                        new Production(
                                Map.ofEntries(
                                        entry(Shield.getInstance(), 2)),
                                0, Map.ofEntries(
                                        entry(Coin.getInstance(), 1),
                                        entry(Servant.getInstance(), 1),
                                        entry(Stone.getInstance(), 1)), 0),
                        3),
                /* 13 */
                new DevelopmentCard(
                        Green.getInstance(), 1,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Shield.getInstance(), 2),
                                entry(Coin.getInstance(), 2))),
                        new Production(
                                Map.ofEntries(
                                        entry(Stone.getInstance(), 1),
                                        entry(Servant.getInstance(), 1)),
                                0, Map.ofEntries(
                                        entry(Coin.getInstance(), 2),
                                        entry(Faith.getInstance(), 1)), 0),
                        4),
                /* 14 */
                new DevelopmentCard(
                        Purple.getInstance(), 1,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Servant.getInstance(), 2),
                                entry(Stone.getInstance(), 2))),
                        new Production(
                                Map.ofEntries(
                                        entry(Coin.getInstance(), 1),
                                        entry(Shield.getInstance(), 1)),
                                0, Map.ofEntries(
                                        entry(Stone.getInstance(), 2),
                                        entry(Faith.getInstance(), 1)), 0),
                        4),
                /* 15 */
                new DevelopmentCard(
                        Blue.getInstance(), 1,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Coin.getInstance(), 2),
                                entry(Servant.getInstance(), 2))),
                        new Production(
                                Map.ofEntries(
                                        entry(Shield.getInstance(), 1),
                                        entry(Stone.getInstance(), 1)),
                                0, Map.ofEntries(
                                        entry(Servant.getInstance(), 2),
                                        entry(Faith.getInstance(), 1)), 0),
                        4),
                /* 16 */
                new DevelopmentCard(
                        Yellow.getInstance(), 1,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Stone.getInstance(), 2),
                                entry(Shield.getInstance(), 2))),
                        new Production(
                                Map.ofEntries(
                                        entry(Coin.getInstance(), 1),
                                        entry(Servant.getInstance(), 1)),
                                0, Map.ofEntries(
                                        entry(Shield.getInstance(), 2),
                                        entry(Faith.getInstance(), 1)), 0),
                        4),
                /* 17 */
                new DevelopmentCard(
                        Green.getInstance(), 2,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Shield.getInstance(), 4))),
                        new Production(
                                Map.ofEntries(
                                        entry(Stone.getInstance(), 1)),
                                0, Map.ofEntries(
                                        entry(Faith.getInstance(), 2)), 0),
                        5),
                /* 18 */
                new DevelopmentCard(
                        Purple.getInstance(), 2,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Servant.getInstance(), 4))),
                        new Production(
                                Map.ofEntries(
                                        entry(Coin.getInstance(), 1)),
                                0, Map.ofEntries(
                                        entry(Faith.getInstance(), 2)), 0),
                        5),
                /* 19 */
                new DevelopmentCard(
                        Blue.getInstance(), 2,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Coin.getInstance(), 4))),
                        new Production(
                                Map.ofEntries(
                                        entry(Servant.getInstance(), 1)),
                                0, Map.ofEntries(
                                        entry(Faith.getInstance(), 2)), 0),
                        5),
                /* 20 */
                new DevelopmentCard(
                        Yellow.getInstance(), 2,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Stone.getInstance(), 4))),
                        new Production(
                                Map.ofEntries(
                                        entry(Shield.getInstance(), 1)),
                                0, Map.ofEntries(
                                        entry(Faith.getInstance(), 2)), 0),
                        5),
                /* 21 */
                new DevelopmentCard(
                        Green.getInstance(), 2,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Shield.getInstance(), 3),
                                entry(Servant.getInstance(), 2))),
                        new Production(
                                Map.ofEntries(
                                        entry(Shield.getInstance(), 1),
                                        entry(Servant.getInstance(), 1)),
                                0, Map.ofEntries(
                                        entry(Stone.getInstance(), 3)), 0),
                        6),
                /* 22 */
                new DevelopmentCard(
                        Purple.getInstance(), 2,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Servant.getInstance(), 3),
                                entry(Coin.getInstance(), 2))),
                        new Production(
                                Map.ofEntries(
                                        entry(Coin.getInstance(), 1),
                                        entry(Servant.getInstance(), 1)),
                                0, Map.ofEntries(
                                        entry(Shield.getInstance(), 3)), 0),
                        6),
                /* 23 */
                new DevelopmentCard(
                        Blue.getInstance(), 2,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Coin.getInstance(), 3),
                                entry(Stone.getInstance(), 2))),
                        new Production(
                                Map.ofEntries(
                                        entry(Coin.getInstance(), 1),
                                        entry(Stone.getInstance(), 1)),
                                0, Map.ofEntries(
                                        entry(Servant.getInstance(), 3)), 0),
                        6),
                /* 24 */
                new DevelopmentCard(
                        Yellow.getInstance(), 2,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Stone.getInstance(), 3),
                                entry(Shield.getInstance(), 2))),
                        new Production(
                                Map.ofEntries(
                                        entry(Stone.getInstance(), 1),
                                        entry(Shield.getInstance(), 1)),
                                0, Map.ofEntries(
                                        entry(Coin.getInstance(), 3)), 0),
                        6),
                /* 25 */
                new DevelopmentCard(
                        Green.getInstance(), 2,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Shield.getInstance(), 5))),
                        new Production(
                                Map.ofEntries(
                                        entry(Coin.getInstance(), 2)),
                                0, Map.ofEntries(
                                        entry(Stone.getInstance(), 2),
                                        entry(Faith.getInstance(), 2)), 0),
                        7),
                /* 26 */
                new DevelopmentCard(
                        Purple.getInstance(), 2,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Servant.getInstance(), 5))),
                        new Production(
                                Map.ofEntries(
                                        entry(Stone.getInstance(), 1)),
                                0, Map.ofEntries(
                                        entry(Coin.getInstance(), 2),
                                        entry(Faith.getInstance(), 2)), 0),
                        7),
                /* 27 */
                new DevelopmentCard(
                        Blue.getInstance(), 2,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Coin.getInstance(), 5))),
                        new Production(
                                Map.ofEntries(
                                        entry(Servant.getInstance(), 2)),
                                0, Map.ofEntries(
                                        entry(Shield.getInstance(), 2),
                                        entry(Faith.getInstance(), 2)), 0),
                        7),
                /* 28 */
                new DevelopmentCard(
                        Yellow.getInstance(), 2,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Stone.getInstance(), 5))),
                        new Production(
                                Map.ofEntries(
                                        entry(Shield.getInstance(), 2)),
                                0, Map.ofEntries(
                                        entry(Servant.getInstance(), 2),
                                        entry(Faith.getInstance(), 2)), 0),
                        7),
                /* 29 */
                new DevelopmentCard(
                        Green.getInstance(), 2,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Shield.getInstance(), 3),
                                entry(Coin.getInstance(), 3))),
                        new Production(
                                Map.ofEntries(
                                        entry(Coin.getInstance(), 1)),
                                0, Map.ofEntries(
                                        entry(Shield.getInstance(), 2),
                                        entry(Faith.getInstance(), 1)), 0),
                        8),
                /* 30 */
                new DevelopmentCard(
                        Purple.getInstance(), 2,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Servant.getInstance(), 3),
                                entry(Shield.getInstance(), 3))),
                        new Production(
                                Map.ofEntries(
                                        entry(Stone.getInstance(), 1)),
                                0, Map.ofEntries(
                                        entry(Servant.getInstance(), 2),
                                        entry(Faith.getInstance(), 1)), 0),
                        8),
                /* 31 */
                new DevelopmentCard(
                        Blue.getInstance(), 2,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Coin.getInstance(), 3),
                                entry(Stone.getInstance(), 3))),
                        new Production(
                                Map.ofEntries(
                                        entry(Servant.getInstance(), 1)),
                                0, Map.ofEntries(
                                        entry(Stone.getInstance(), 2),
                                        entry(Faith.getInstance(), 1)), 0),
                        8),
                /* 32 */
                new DevelopmentCard(
                        Yellow.getInstance(), 2,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Stone.getInstance(), 3),
                                entry(Servant.getInstance(), 3))),
                        new Production(
                                Map.ofEntries(
                                        entry(Shield.getInstance(), 1)),
                                0, Map.ofEntries(
                                        entry(Coin.getInstance(), 2),
                                        entry(Faith.getInstance(), 1)), 0),
                        8),
                /* 33 */
                new DevelopmentCard(
                        Green.getInstance(), 3,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Shield.getInstance(), 6))),
                        new Production(
                                Map.ofEntries(
                                        entry(Coin.getInstance(), 2)),
                                0, Map.ofEntries(
                                        entry(Stone.getInstance(), 3),
                                        entry(Faith.getInstance(), 2)), 0),
                        9),
                /* 34 */
                new DevelopmentCard(
                        Purple.getInstance(), 3,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Servant.getInstance(), 6))),
                        new Production(
                                Map.ofEntries(
                                        entry(Stone.getInstance(), 2)),
                                0, Map.ofEntries(
                                        entry(Coin.getInstance(), 3),
                                        entry(Faith.getInstance(), 2)), 0),
                        9),
                /* 35 */
                new DevelopmentCard(
                        Blue.getInstance(), 3,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Coin.getInstance(), 6))),
                        new Production(
                                Map.ofEntries(
                                        entry(Servant.getInstance(), 2)),
                                0, Map.ofEntries(
                                        entry(Shield.getInstance(), 3),
                                        entry(Faith.getInstance(), 2)), 0),
                        9),
                /* 36 */
                new DevelopmentCard(
                        Yellow.getInstance(), 3,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Stone.getInstance(), 6))),
                        new Production(
                                Map.ofEntries(
                                        entry(Shield.getInstance(), 2)),
                                0, Map.ofEntries(
                                        entry(Servant.getInstance(), 3),
                                        entry(Faith.getInstance(), 2)), 0),
                        9),
                /* 37 */
                new DevelopmentCard(
                        Green.getInstance(), 3,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Shield.getInstance(), 5),
                                entry(Servant.getInstance(), 2))),
                        new Production(
                                Map.ofEntries(
                                        entry(Coin.getInstance(), 1),
                                        entry(Servant.getInstance(), 1)),
                                0, Map.ofEntries(
                                        entry(Shield.getInstance(), 2),
                                        entry(Stone.getInstance(), 2),
                                        entry(Faith.getInstance(), 1)), 0),
                        10),
                /* 38 */
                new DevelopmentCard(
                        Purple.getInstance(), 3,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Servant.getInstance(), 5),
                                entry(Coin.getInstance(), 2))),
                        new Production(
                                Map.ofEntries(
                                        entry(Stone.getInstance(), 1),
                                        entry(Shield.getInstance(), 1)),
                                0, Map.ofEntries(
                                        entry(Coin.getInstance(), 2),
                                        entry(Servant.getInstance(), 2),
                                        entry(Faith.getInstance(), 1)), 0),
                        10),
                /* 39 */
                new DevelopmentCard(
                        Blue.getInstance(), 3,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Coin.getInstance(), 5),
                                entry(Stone.getInstance(), 2))),
                        new Production(
                                Map.ofEntries(
                                        entry(Coin.getInstance(), 1),
                                        entry(Shield.getInstance(), 1)),
                                0, Map.ofEntries(
                                        entry(Servant.getInstance(), 2),
                                        entry(Stone.getInstance(), 2),
                                        entry(Faith.getInstance(), 1)), 0),
                        10),
                /* 40 */
                new DevelopmentCard(
                        Yellow.getInstance(), 3,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Stone.getInstance(), 5),
                                entry(Servant.getInstance(), 2))),
                        new Production(
                                Map.ofEntries(
                                        entry(Stone.getInstance(), 1),
                                        entry(Servant.getInstance(), 1)),
                                0, Map.ofEntries(
                                        entry(Coin.getInstance(), 2),
                                        entry(Shield.getInstance(), 2),
                                        entry(Faith.getInstance(), 1)), 0),
                        10),
                /* 41 */
                new DevelopmentCard(
                        Green.getInstance(), 3,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Shield.getInstance(), 7))),
                        new Production(
                                Map.ofEntries(
                                        entry(Servant.getInstance(), 1)),
                                0, Map.ofEntries(
                                        entry(Coin.getInstance(), 1),
                                        entry(Faith.getInstance(), 3)), 0),
                        11),
                /* 42 */
                new DevelopmentCard(
                        Purple.getInstance(), 3,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Servant.getInstance(), 7))),
                        new Production(
                                Map.ofEntries(
                                        entry(Coin.getInstance(), 1)),
                                0, Map.ofEntries(
                                        entry(Stone.getInstance(), 1),
                                        entry(Faith.getInstance(), 3)), 0),
                        11),
                /* 43 */
                new DevelopmentCard(
                        Blue.getInstance(), 3,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Coin.getInstance(), 7))),
                        new Production(
                                Map.ofEntries(
                                        entry(Stone.getInstance(), 1)),
                                0, Map.ofEntries(
                                        entry(Shield.getInstance(), 1),
                                        entry(Faith.getInstance(), 3)), 0),
                        11),
                /* 44 */
                new DevelopmentCard(
                        Yellow.getInstance(), 3,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Stone.getInstance(), 7))),
                        new Production(
                                Map.ofEntries(
                                        entry(Shield.getInstance(), 1)),
                                0, Map.ofEntries(
                                        entry(Servant.getInstance(), 1),
                                        entry(Faith.getInstance(), 3)), 0),
                        11),
                /* 45 */
                new DevelopmentCard(
                        Green.getInstance(), 3,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Shield.getInstance(), 4),
                                entry(Coin.getInstance(), 4))),
                        new Production(
                                Map.ofEntries(
                                        entry(Stone.getInstance(), 1)),
                                0, Map.ofEntries(
                                        entry(Coin.getInstance(), 3),
                                        entry(Shield.getInstance(), 1)), 0),
                        12),
                /* 46 */
                new DevelopmentCard(
                        Purple.getInstance(), 3,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Servant.getInstance(), 4),
                                entry(Shield.getInstance(), 4))),
                        new Production(
                                Map.ofEntries(
                                        entry(Coin.getInstance(), 1)),
                                0, Map.ofEntries(
                                        entry(Stone.getInstance(), 3),
                                        entry(Servant.getInstance(), 1)), 0),
                        12),
                /* 47 */
                new DevelopmentCard(
                        Blue.getInstance(), 3,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Coin.getInstance(), 4),
                                entry(Stone.getInstance(), 4))),
                        new Production(
                                Map.ofEntries(
                                        entry(Servant.getInstance(), 1)),
                                0, Map.ofEntries(
                                        entry(Coin.getInstance(), 1),
                                        entry(Shield.getInstance(), 3)), 0),
                        12),
                /* 48 */
                new DevelopmentCard(
                        Yellow.getInstance(), 3,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Stone.getInstance(), 4),
                                entry(Servant.getInstance(), 4))),
                        new Production(
                                Map.ofEntries(
                                        entry(Shield.getInstance(), 1)),
                                0, Map.ofEntries(
                                        entry(Stone.getInstance(), 1),
                                        entry(Servant.getInstance(), 3)), 0),
                        12)
        );
    }

    /**
     * Returns a list of all possible leader cards.
     *
     * @return  list of leader cards
     */
    public List<LeaderCard> generateLeaderCards() {
        return List.of(
                /* 49 */
                new DiscountLeader(1, Servant.getInstance(), new DevCardRequirement(
                        List.of(
                                new DevCardRequirement.Entry(Yellow.getInstance(), 0, 1),
                                new DevCardRequirement.Entry(Green.getInstance(), 0, 1)
                )), 2),
                /* 50 */
                new DiscountLeader(1, Shield.getInstance(), new DevCardRequirement(
                        List.of(
                                new DevCardRequirement.Entry(Blue.getInstance(), 0, 1),
                                new DevCardRequirement.Entry(Purple.getInstance(), 0, 1)
                )), 2),
                /* 51 */
                new DiscountLeader(1, Stone.getInstance(), new DevCardRequirement(
                        List.of(
                                new DevCardRequirement.Entry(Green.getInstance(), 0, 1),
                                new DevCardRequirement.Entry(Blue.getInstance(), 0, 1)
                )), 2),
                /* 52 */
                new DiscountLeader(1, Coin.getInstance(), new DevCardRequirement(
                        List.of(
                                new DevCardRequirement.Entry(Yellow.getInstance(), 0, 1),
                                new DevCardRequirement.Entry(Purple.getInstance(), 0, 1)
                )), 2),
                /* 53 */
                new DepotLeader(2, Stone.getInstance(), new ResourceRequirement(Map.ofEntries(
                        entry(Coin.getInstance(), 5)
                )), 3),
                /* 54 */
                new DepotLeader(2, Servant.getInstance(), new ResourceRequirement(Map.ofEntries(
                        entry(Stone.getInstance(), 5)
                )), 3),
                /* 55 */
                new DepotLeader(2, Shield.getInstance(), new ResourceRequirement(Map.ofEntries(
                        entry(Servant.getInstance(), 5)
                )), 3),
                /* 56 */
                new DepotLeader(2, Coin.getInstance(), new ResourceRequirement(Map.ofEntries(
                        entry(Shield.getInstance(), 5)
                )), 3),
                /* 57 */
                new ZeroLeader(Servant.getInstance(), new DevCardRequirement(
                        List.of(
                                new DevCardRequirement.Entry(Yellow.getInstance(), 0, 2),
                                new DevCardRequirement.Entry(Blue.getInstance(), 0, 1)
                )), 5),
                /* 58 */
                new ZeroLeader(Shield.getInstance(), new DevCardRequirement(
                        List.of(
                                new DevCardRequirement.Entry(Green.getInstance(), 0, 2),
                                new DevCardRequirement.Entry(Purple.getInstance(), 0, 1)
                )), 5),
                /* 59 */
                new ZeroLeader(Stone.getInstance(), new DevCardRequirement(
                        List.of(
                                new DevCardRequirement.Entry(Blue.getInstance(), 0, 2),
                                new DevCardRequirement.Entry(Yellow.getInstance(), 0, 1)
                )), 5),
                /* 60 */
                new ZeroLeader(Coin.getInstance(), new DevCardRequirement(
                        List.of(
                                new DevCardRequirement.Entry(Purple.getInstance(), 0, 2),
                                new DevCardRequirement.Entry(Green.getInstance(), 0, 1)
                )), 5),
                /* 61 */
                new ProductionLeader(
                        new Production(
                                Map.ofEntries(
                                        entry(Shield.getInstance(), 1)),
                                0,
                                Map.ofEntries(
                                        entry(Faith.getInstance(), 1)),
                                1),
                        Servant.getInstance(), new DevCardRequirement(
                                List.of(
                                new DevCardRequirement.Entry(Yellow.getInstance(), 2, 1)
                )), 4),
                /* 62 */
                new ProductionLeader(
                        new Production(
                                Map.ofEntries(
                                        entry(Servant.getInstance(), 1)),
                                0,
                                Map.ofEntries(
                                        entry(Faith.getInstance(), 1)),
                                1),
                        Servant.getInstance(), new DevCardRequirement(
                                List.of(
                                        new DevCardRequirement.Entry(Blue.getInstance(), 2, 1)
                )), 4),
                /* 63 */
                new ProductionLeader(
                        new Production(
                                Map.ofEntries(
                                        entry(Stone.getInstance(), 1)),
                                0,
                                Map.ofEntries(
                                        entry(Faith.getInstance(), 1)),
                                1),
                        Servant.getInstance(), new DevCardRequirement(
                                List.of(
                                new DevCardRequirement.Entry(Purple.getInstance(), 2, 1)
                )), 4),
                /* 64 */
                new ProductionLeader(
                        new Production(
                                Map.ofEntries(
                                        entry(Coin.getInstance(), 1)),
                                0,
                                Map.ofEntries(
                                        entry(Faith.getInstance(), 1)),
                                1),
                        Servant.getInstance(), new DevCardRequirement(
                                List.of(
                                new DevCardRequirement.Entry(Green.getInstance(), 2, 1)
                )), 4)
        );
    }

    /**
     * Returns a map of the resources inside the market.
     *
     * @return  map of the resources to put inside the market
     */
    private Map<ResourceType, Integer> generateMarketResources() {
        return Map.ofEntries(
                entry(Coin.getInstance(), 2),
                entry(Faith.getInstance(), 1),
                entry(Servant.getInstance(), 2),
                entry(Shield.getInstance(), 2),
                entry(Stone.getInstance(), 2),
                entry(Zero.getInstance(), 4)
        );
    }

    /**
     * Returns a map of the vatican sections.
     *
     * @return  map of the vatican sections
     */
    public Map<Integer, Integer[]> generateVaticanSections() {
        return Map.ofEntries(
                entry(8, new Integer[]{5, 2}),
                entry(16, new Integer[]{12, 3}),
                entry(24, new Integer[]{19, 4})
        );
    }

    /**
     * Returns a map of the yellow tiles.
     *
     * @return  map of the yellow tiles
     */
    public Map<Integer, Integer> generateYellowTiles() {
        return Map.ofEntries(
                entry(3, 1),
                entry(6, 2),
                entry(9, 4),
                entry(12,6),
                entry(15,9),
                entry(18,12),
                entry(21,16),
                entry(24,20)
        );
    }

    /*
     * Returns a list of the action tokens.
     *
     * @return  list of action tokens
     */
    public List<ActionToken> generateActionTokens() {
        return List.of(
                new ActionTokenBlackMoveTwo(),
                new ActionTokenBlackMoveTwo(),
                new ActionTokenBlackMoveOneShuffle(),
                new ActionTokenDiscardTwo(Blue.getInstance()),
                new ActionTokenDiscardTwo(Green.getInstance()),
                new ActionTokenDiscardTwo(Purple.getInstance()),
                new ActionTokenDiscardTwo(Yellow.getInstance())
        );
    }
}
