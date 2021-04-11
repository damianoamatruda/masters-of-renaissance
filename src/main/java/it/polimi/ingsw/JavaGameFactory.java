package it.polimi.ingsw;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.actiontokens.ActionToken;
import it.polimi.ingsw.model.actiontokens.ActionTokenBlackMoveOneShuffle;
import it.polimi.ingsw.model.actiontokens.ActionTokenBlackMoveTwo;
import it.polimi.ingsw.model.actiontokens.ActionTokenDiscardTwo;
import it.polimi.ingsw.model.cardrequirements.DevCardRequirement;
import it.polimi.ingsw.model.cardrequirements.ResourceRequirement;
import it.polimi.ingsw.model.devcardcolors.*;
import it.polimi.ingsw.model.leadercards.*;
import it.polimi.ingsw.model.resourcecontainers.Strongbox;
import it.polimi.ingsw.model.resourcecontainers.Warehouse;
import it.polimi.ingsw.model.resourcetypes.*;

import java.util.*;

import static java.util.Map.entry;

/**
 * This class builds games with the original parameters.
 */
public class JavaGameFactory implements GameFactory {
    /** The factory of resource types. */
    private final ResourceTypeFactory resTypeFactory;

    /** The factory of development card colors. */
    private final DevCardColorFactory devCardColorFactory;
    
    /** Maximum number of players that can connect to the same game instance. */
    private final int maxPlayersCount = 4;

    /** Number of distinct leader cards given to each player at the beginning of the game. */
    private final int playerLeadersCount = 4;
    
    public JavaGameFactory() {
        resTypeFactory = new JavaResourceTypeFactory();
        devCardColorFactory = new JavaDevCardColorFactory();
    }

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
                new Market(generateMarketResources(), 4, resTypeFactory.get("Zero")),
                new FaithTrack(generateVaticanSections(), generateYellowTiles()), 24,
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
                new Market(generateMarketResources(), 4, resTypeFactory.get("Zero")),
                new FaithTrack(generateVaticanSections(), generateYellowTiles()), generateActionTokens(), 24,
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
     * Getter of the factory of resources.
     *
     * @return the factory of resources.
     */
    public ResourceTypeFactory getResTypeFactory() {
        return resTypeFactory;
    }

    /**
     * Getter of the factory of development colors.
     *
     * @return the factory of development colors.
     */
    public DevCardColorFactory getDevCardColorFactory() {
        return devCardColorFactory;
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
                        devCardColorFactory.get("Green"), 1,
                        new ResourceRequirement(Map.ofEntries(
                                entry(resTypeFactory.get("Shield"), 2))),
                        new Production(
                                Map.ofEntries(
                                        entry(resTypeFactory.get("Coin"), 1)),
                                0, Map.ofEntries(
                                        entry(resTypeFactory.get("Faith"), 1)), 0),
                        1),
                /* 2 */
                new DevelopmentCard(
                        devCardColorFactory.get("Purple"), 1,
                        new ResourceRequirement(Map.ofEntries(
                                entry(resTypeFactory.get("Servant"), 2))),
                        new Production(
                                Map.ofEntries(
                                        entry(resTypeFactory.get("Stone"), 1)),
                                0, Map.ofEntries(
                                        entry(resTypeFactory.get("Faith"), 1)), 0),
                        1),
                /* 3 */
                new DevelopmentCard(
                        devCardColorFactory.get("Blue"), 1,
                        new ResourceRequirement(Map.ofEntries(
                                entry(resTypeFactory.get("Coin"), 2))),
                        new Production(
                                Map.ofEntries(
                                        entry(resTypeFactory.get("Shield"), 1)),
                                0, Map.ofEntries(
                                        entry(resTypeFactory.get("Faith"), 1)), 0),
                        1),
                /* 4 */
                new DevelopmentCard(
                        devCardColorFactory.get("Yellow"), 1,
                        new ResourceRequirement(Map.ofEntries(
                                entry(resTypeFactory.get("Stone"), 2))),
                        new Production(
                                Map.ofEntries(
                                        entry(resTypeFactory.get("Servant"), 1)),
                                0, Map.ofEntries(
                                        entry(resTypeFactory.get("Faith"), 1)), 0),
                        1),
                /* 5 */
                new DevelopmentCard(
                        devCardColorFactory.get("Green"), 1,
                        new ResourceRequirement(Map.ofEntries(
                                entry(resTypeFactory.get("Shield"), 1),
                                entry(resTypeFactory.get("Servant"), 1),
                                entry(resTypeFactory.get("Stone"), 1))),
                        new Production(
                                Map.ofEntries(
                                        entry(resTypeFactory.get("Stone"), 1)),
                                0, Map.ofEntries(
                                        entry(resTypeFactory.get("Servant"), 1)), 0),
                        2),
                /* 6 */
                new DevelopmentCard(
                        devCardColorFactory.get("Purple"), 1,
                        new ResourceRequirement(Map.ofEntries(
                                entry(resTypeFactory.get("Shield"), 1),
                                entry(resTypeFactory.get("Servant"), 1),
                                entry(resTypeFactory.get("Coin"), 1))),
                        new Production(
                                Map.ofEntries(
                                        entry(resTypeFactory.get("Coin"), 1)),
                                0, Map.ofEntries(
                                        entry(resTypeFactory.get("Shield"), 1)), 0),
                        2),
                /* 7 */
                new DevelopmentCard(
                        devCardColorFactory.get("Blue"), 1,
                        new ResourceRequirement(Map.ofEntries(
                                entry(resTypeFactory.get("Coin"), 1),
                                entry(resTypeFactory.get("Servant"), 1),
                                entry(resTypeFactory.get("Stone"), 1))),
                        new Production(
                                Map.ofEntries(
                                        entry(resTypeFactory.get("Servant"), 1)),
                                0, Map.ofEntries(
                                        entry(resTypeFactory.get("Stone"), 1)), 0),
                        2),
                /* 8 */
                new DevelopmentCard(
                        devCardColorFactory.get("Yellow"), 1,
                        new ResourceRequirement(Map.ofEntries(
                                entry(resTypeFactory.get("Shield"), 1),
                                entry(resTypeFactory.get("Stone"), 1),
                                entry(resTypeFactory.get("Coin"), 1))),
                        new Production(
                                Map.ofEntries(
                                        entry(resTypeFactory.get("Shield"), 1)),
                                0, Map.ofEntries(
                                        entry(resTypeFactory.get("Coin"), 1)), 0),
                        2),
                /* 9 */
                new DevelopmentCard(
                        devCardColorFactory.get("Green"), 1,
                        new ResourceRequirement(Map.ofEntries(
                                entry(resTypeFactory.get("Shield"), 3))),
                        new Production(
                                Map.ofEntries(
                                        entry(resTypeFactory.get("Servant"), 2)),
                                0, Map.ofEntries(
                                        entry(resTypeFactory.get("Coin"), 1),
                                        entry(resTypeFactory.get("Shield"), 1),
                                        entry(resTypeFactory.get("Stone"), 1)), 0),
                        3),
                /* 10 */
                new DevelopmentCard(
                        devCardColorFactory.get("Purple"), 1,
                        new ResourceRequirement(Map.ofEntries(
                                entry(resTypeFactory.get("Servant"), 3))),
                        new Production(
                                Map.ofEntries(
                                        entry(resTypeFactory.get("Coin"), 2)),
                                0, Map.ofEntries(
                                        entry(resTypeFactory.get("Servant"), 1),
                                        entry(resTypeFactory.get("Shield"), 1),
                                        entry(resTypeFactory.get("Stone"), 1)), 0),
                        3),
                /* 11 */
                new DevelopmentCard(
                        devCardColorFactory.get("Blue"), 1,
                        new ResourceRequirement(Map.ofEntries(
                                entry(resTypeFactory.get("Coin"), 3))),
                        new Production(
                                Map.ofEntries(
                                        entry(resTypeFactory.get("Stone"), 2)),
                                0, Map.ofEntries(
                                        entry(resTypeFactory.get("Coin"), 1),
                                        entry(resTypeFactory.get("Servant"), 1),
                                        entry(resTypeFactory.get("Stone"), 1)), 0),
                        3),
                /* 12 */
                new DevelopmentCard(
                        devCardColorFactory.get("Yellow"), 1,
                        new ResourceRequirement(Map.ofEntries(
                                entry(resTypeFactory.get("Stone"), 3))),
                        new Production(
                                Map.ofEntries(
                                        entry(resTypeFactory.get("Shield"), 2)),
                                0, Map.ofEntries(
                                        entry(resTypeFactory.get("Coin"), 1),
                                        entry(resTypeFactory.get("Servant"), 1),
                                        entry(resTypeFactory.get("Stone"), 1)), 0),
                        3),
                /* 13 */
                new DevelopmentCard(
                        devCardColorFactory.get("Green"), 1,
                        new ResourceRequirement(Map.ofEntries(
                                entry(resTypeFactory.get("Shield"), 2),
                                entry(resTypeFactory.get("Coin"), 2))),
                        new Production(
                                Map.ofEntries(
                                        entry(resTypeFactory.get("Stone"), 1),
                                        entry(resTypeFactory.get("Servant"), 1)),
                                0, Map.ofEntries(
                                        entry(resTypeFactory.get("Coin"), 2),
                                        entry(resTypeFactory.get("Faith"), 1)), 0),
                        4),
                /* 14 */
                new DevelopmentCard(
                        devCardColorFactory.get("Purple"), 1,
                        new ResourceRequirement(Map.ofEntries(
                                entry(resTypeFactory.get("Servant"), 2),
                                entry(resTypeFactory.get("Stone"), 2))),
                        new Production(
                                Map.ofEntries(
                                        entry(resTypeFactory.get("Coin"), 1),
                                        entry(resTypeFactory.get("Shield"), 1)),
                                0, Map.ofEntries(
                                        entry(resTypeFactory.get("Stone"), 2),
                                        entry(resTypeFactory.get("Faith"), 1)), 0),
                        4),
                /* 15 */
                new DevelopmentCard(
                        devCardColorFactory.get("Blue"), 1,
                        new ResourceRequirement(Map.ofEntries(
                                entry(resTypeFactory.get("Coin"), 2),
                                entry(resTypeFactory.get("Servant"), 2))),
                        new Production(
                                Map.ofEntries(
                                        entry(resTypeFactory.get("Shield"), 1),
                                        entry(resTypeFactory.get("Stone"), 1)),
                                0, Map.ofEntries(
                                        entry(resTypeFactory.get("Servant"), 2),
                                        entry(resTypeFactory.get("Faith"), 1)), 0),
                        4),
                /* 16 */
                new DevelopmentCard(
                        devCardColorFactory.get("Yellow"), 1,
                        new ResourceRequirement(Map.ofEntries(
                                entry(resTypeFactory.get("Stone"), 2),
                                entry(resTypeFactory.get("Shield"), 2))),
                        new Production(
                                Map.ofEntries(
                                        entry(resTypeFactory.get("Coin"), 1),
                                        entry(resTypeFactory.get("Servant"), 1)),
                                0, Map.ofEntries(
                                        entry(resTypeFactory.get("Shield"), 2),
                                        entry(resTypeFactory.get("Faith"), 1)), 0),
                        4),
                /* 17 */
                new DevelopmentCard(
                        devCardColorFactory.get("Green"), 2,
                        new ResourceRequirement(Map.ofEntries(
                                entry(resTypeFactory.get("Shield"), 4))),
                        new Production(
                                Map.ofEntries(
                                        entry(resTypeFactory.get("Stone"), 1)),
                                0, Map.ofEntries(
                                        entry(resTypeFactory.get("Faith"), 2)), 0),
                        5),
                /* 18 */
                new DevelopmentCard(
                        devCardColorFactory.get("Purple"), 2,
                        new ResourceRequirement(Map.ofEntries(
                                entry(resTypeFactory.get("Servant"), 4))),
                        new Production(
                                Map.ofEntries(
                                        entry(resTypeFactory.get("Coin"), 1)),
                                0, Map.ofEntries(
                                        entry(resTypeFactory.get("Faith"), 2)), 0),
                        5),
                /* 19 */
                new DevelopmentCard(
                        devCardColorFactory.get("Blue"), 2,
                        new ResourceRequirement(Map.ofEntries(
                                entry(resTypeFactory.get("Coin"), 4))),
                        new Production(
                                Map.ofEntries(
                                        entry(resTypeFactory.get("Servant"), 1)),
                                0, Map.ofEntries(
                                        entry(resTypeFactory.get("Faith"), 2)), 0),
                        5),
                /* 20 */
                new DevelopmentCard(
                        devCardColorFactory.get("Yellow"), 2,
                        new ResourceRequirement(Map.ofEntries(
                                entry(resTypeFactory.get("Stone"), 4))),
                        new Production(
                                Map.ofEntries(
                                        entry(resTypeFactory.get("Shield"), 1)),
                                0, Map.ofEntries(
                                        entry(resTypeFactory.get("Faith"), 2)), 0),
                        5),
                /* 21 */
                new DevelopmentCard(
                        devCardColorFactory.get("Green"), 2,
                        new ResourceRequirement(Map.ofEntries(
                                entry(resTypeFactory.get("Shield"), 3),
                                entry(resTypeFactory.get("Servant"), 2))),
                        new Production(
                                Map.ofEntries(
                                        entry(resTypeFactory.get("Shield"), 1),
                                        entry(resTypeFactory.get("Servant"), 1)),
                                0, Map.ofEntries(
                                        entry(resTypeFactory.get("Stone"), 3)), 0),
                        6),
                /* 22 */
                new DevelopmentCard(
                        devCardColorFactory.get("Purple"), 2,
                        new ResourceRequirement(Map.ofEntries(
                                entry(resTypeFactory.get("Servant"), 3),
                                entry(resTypeFactory.get("Coin"), 2))),
                        new Production(
                                Map.ofEntries(
                                        entry(resTypeFactory.get("Coin"), 1),
                                        entry(resTypeFactory.get("Servant"), 1)),
                                0, Map.ofEntries(
                                        entry(resTypeFactory.get("Shield"), 3)), 0),
                        6),
                /* 23 */
                new DevelopmentCard(
                        devCardColorFactory.get("Blue"), 2,
                        new ResourceRequirement(Map.ofEntries(
                                entry(resTypeFactory.get("Coin"), 3),
                                entry(resTypeFactory.get("Stone"), 2))),
                        new Production(
                                Map.ofEntries(
                                        entry(resTypeFactory.get("Coin"), 1),
                                        entry(resTypeFactory.get("Stone"), 1)),
                                0, Map.ofEntries(
                                        entry(resTypeFactory.get("Servant"), 3)), 0),
                        6),
                /* 24 */
                new DevelopmentCard(
                        devCardColorFactory.get("Yellow"), 2,
                        new ResourceRequirement(Map.ofEntries(
                                entry(resTypeFactory.get("Stone"), 3),
                                entry(resTypeFactory.get("Shield"), 2))),
                        new Production(
                                Map.ofEntries(
                                        entry(resTypeFactory.get("Stone"), 1),
                                        entry(resTypeFactory.get("Shield"), 1)),
                                0, Map.ofEntries(
                                        entry(resTypeFactory.get("Coin"), 3)), 0),
                        6),
                /* 25 */
                new DevelopmentCard(
                        devCardColorFactory.get("Green"), 2,
                        new ResourceRequirement(Map.ofEntries(
                                entry(resTypeFactory.get("Shield"), 5))),
                        new Production(
                                Map.ofEntries(
                                        entry(resTypeFactory.get("Coin"), 2)),
                                0, Map.ofEntries(
                                        entry(resTypeFactory.get("Stone"), 2),
                                        entry(resTypeFactory.get("Faith"), 2)), 0),
                        7),
                /* 26 */
                new DevelopmentCard(
                        devCardColorFactory.get("Purple"), 2,
                        new ResourceRequirement(Map.ofEntries(
                                entry(resTypeFactory.get("Servant"), 5))),
                        new Production(
                                Map.ofEntries(
                                        entry(resTypeFactory.get("Stone"), 1)),
                                0, Map.ofEntries(
                                        entry(resTypeFactory.get("Coin"), 2),
                                        entry(resTypeFactory.get("Faith"), 2)), 0),
                        7),
                /* 27 */
                new DevelopmentCard(
                        devCardColorFactory.get("Blue"), 2,
                        new ResourceRequirement(Map.ofEntries(
                                entry(resTypeFactory.get("Coin"), 5))),
                        new Production(
                                Map.ofEntries(
                                        entry(resTypeFactory.get("Servant"), 2)),
                                0, Map.ofEntries(
                                        entry(resTypeFactory.get("Shield"), 2),
                                        entry(resTypeFactory.get("Faith"), 2)), 0),
                        7),
                /* 28 */
                new DevelopmentCard(
                        devCardColorFactory.get("Yellow"), 2,
                        new ResourceRequirement(Map.ofEntries(
                                entry(resTypeFactory.get("Stone"), 5))),
                        new Production(
                                Map.ofEntries(
                                        entry(resTypeFactory.get("Shield"), 2)),
                                0, Map.ofEntries(
                                        entry(resTypeFactory.get("Servant"), 2),
                                        entry(resTypeFactory.get("Faith"), 2)), 0),
                        7),
                /* 29 */
                new DevelopmentCard(
                        devCardColorFactory.get("Green"), 2,
                        new ResourceRequirement(Map.ofEntries(
                                entry(resTypeFactory.get("Shield"), 3),
                                entry(resTypeFactory.get("Coin"), 3))),
                        new Production(
                                Map.ofEntries(
                                        entry(resTypeFactory.get("Coin"), 1)),
                                0, Map.ofEntries(
                                        entry(resTypeFactory.get("Shield"), 2),
                                        entry(resTypeFactory.get("Faith"), 1)), 0),
                        8),
                /* 30 */
                new DevelopmentCard(
                        devCardColorFactory.get("Purple"), 2,
                        new ResourceRequirement(Map.ofEntries(
                                entry(resTypeFactory.get("Servant"), 3),
                                entry(resTypeFactory.get("Shield"), 3))),
                        new Production(
                                Map.ofEntries(
                                        entry(resTypeFactory.get("Stone"), 1)),
                                0, Map.ofEntries(
                                        entry(resTypeFactory.get("Servant"), 2),
                                        entry(resTypeFactory.get("Faith"), 1)), 0),
                        8),
                /* 31 */
                new DevelopmentCard(
                        devCardColorFactory.get("Blue"), 2,
                        new ResourceRequirement(Map.ofEntries(
                                entry(resTypeFactory.get("Coin"), 3),
                                entry(resTypeFactory.get("Stone"), 3))),
                        new Production(
                                Map.ofEntries(
                                        entry(resTypeFactory.get("Servant"), 1)),
                                0, Map.ofEntries(
                                        entry(resTypeFactory.get("Stone"), 2),
                                        entry(resTypeFactory.get("Faith"), 1)), 0),
                        8),
                /* 32 */
                new DevelopmentCard(
                        devCardColorFactory.get("Yellow"), 2,
                        new ResourceRequirement(Map.ofEntries(
                                entry(resTypeFactory.get("Stone"), 3),
                                entry(resTypeFactory.get("Servant"), 3))),
                        new Production(
                                Map.ofEntries(
                                        entry(resTypeFactory.get("Shield"), 1)),
                                0, Map.ofEntries(
                                        entry(resTypeFactory.get("Coin"), 2),
                                        entry(resTypeFactory.get("Faith"), 1)), 0),
                        8),
                /* 33 */
                new DevelopmentCard(
                        devCardColorFactory.get("Green"), 3,
                        new ResourceRequirement(Map.ofEntries(
                                entry(resTypeFactory.get("Shield"), 6))),
                        new Production(
                                Map.ofEntries(
                                        entry(resTypeFactory.get("Coin"), 2)),
                                0, Map.ofEntries(
                                        entry(resTypeFactory.get("Stone"), 3),
                                        entry(resTypeFactory.get("Faith"), 2)), 0),
                        9),
                /* 34 */
                new DevelopmentCard(
                        devCardColorFactory.get("Purple"), 3,
                        new ResourceRequirement(Map.ofEntries(
                                entry(resTypeFactory.get("Servant"), 6))),
                        new Production(
                                Map.ofEntries(
                                        entry(resTypeFactory.get("Stone"), 2)),
                                0, Map.ofEntries(
                                        entry(resTypeFactory.get("Coin"), 3),
                                        entry(resTypeFactory.get("Faith"), 2)), 0),
                        9),
                /* 35 */
                new DevelopmentCard(
                        devCardColorFactory.get("Blue"), 3,
                        new ResourceRequirement(Map.ofEntries(
                                entry(resTypeFactory.get("Coin"), 6))),
                        new Production(
                                Map.ofEntries(
                                        entry(resTypeFactory.get("Servant"), 2)),
                                0, Map.ofEntries(
                                        entry(resTypeFactory.get("Shield"), 3),
                                        entry(resTypeFactory.get("Faith"), 2)), 0),
                        9),
                /* 36 */
                new DevelopmentCard(
                        devCardColorFactory.get("Yellow"), 3,
                        new ResourceRequirement(Map.ofEntries(
                                entry(resTypeFactory.get("Stone"), 6))),
                        new Production(
                                Map.ofEntries(
                                        entry(resTypeFactory.get("Shield"), 2)),
                                0, Map.ofEntries(
                                        entry(resTypeFactory.get("Servant"), 3),
                                        entry(resTypeFactory.get("Faith"), 2)), 0),
                        9),
                /* 37 */
                new DevelopmentCard(
                        devCardColorFactory.get("Green"), 3,
                        new ResourceRequirement(Map.ofEntries(
                                entry(resTypeFactory.get("Shield"), 5),
                                entry(resTypeFactory.get("Servant"), 2))),
                        new Production(
                                Map.ofEntries(
                                        entry(resTypeFactory.get("Coin"), 1),
                                        entry(resTypeFactory.get("Servant"), 1)),
                                0, Map.ofEntries(
                                        entry(resTypeFactory.get("Shield"), 2),
                                        entry(resTypeFactory.get("Stone"), 2),
                                        entry(resTypeFactory.get("Faith"), 1)), 0),
                        10),
                /* 38 */
                new DevelopmentCard(
                        devCardColorFactory.get("Purple"), 3,
                        new ResourceRequirement(Map.ofEntries(
                                entry(resTypeFactory.get("Servant"), 5),
                                entry(resTypeFactory.get("Coin"), 2))),
                        new Production(
                                Map.ofEntries(
                                        entry(resTypeFactory.get("Stone"), 1),
                                        entry(resTypeFactory.get("Shield"), 1)),
                                0, Map.ofEntries(
                                        entry(resTypeFactory.get("Coin"), 2),
                                        entry(resTypeFactory.get("Servant"), 2),
                                        entry(resTypeFactory.get("Faith"), 1)), 0),
                        10),
                /* 39 */
                new DevelopmentCard(
                        devCardColorFactory.get("Blue"), 3,
                        new ResourceRequirement(Map.ofEntries(
                                entry(resTypeFactory.get("Coin"), 5),
                                entry(resTypeFactory.get("Stone"), 2))),
                        new Production(
                                Map.ofEntries(
                                        entry(resTypeFactory.get("Coin"), 1),
                                        entry(resTypeFactory.get("Shield"), 1)),
                                0, Map.ofEntries(
                                        entry(resTypeFactory.get("Servant"), 2),
                                        entry(resTypeFactory.get("Stone"), 2),
                                        entry(resTypeFactory.get("Faith"), 1)), 0),
                        10),
                /* 40 */
                new DevelopmentCard(
                        devCardColorFactory.get("Yellow"), 3,
                        new ResourceRequirement(Map.ofEntries(
                                entry(resTypeFactory.get("Stone"), 5),
                                entry(resTypeFactory.get("Servant"), 2))),
                        new Production(
                                Map.ofEntries(
                                        entry(resTypeFactory.get("Stone"), 1),
                                        entry(resTypeFactory.get("Servant"), 1)),
                                0, Map.ofEntries(
                                        entry(resTypeFactory.get("Coin"), 2),
                                        entry(resTypeFactory.get("Shield"), 2),
                                        entry(resTypeFactory.get("Faith"), 1)), 0),
                        10),
                /* 41 */
                new DevelopmentCard(
                        devCardColorFactory.get("Green"), 3,
                        new ResourceRequirement(Map.ofEntries(
                                entry(resTypeFactory.get("Shield"), 7))),
                        new Production(
                                Map.ofEntries(
                                        entry(resTypeFactory.get("Servant"), 1)),
                                0, Map.ofEntries(
                                        entry(resTypeFactory.get("Coin"), 1),
                                        entry(resTypeFactory.get("Faith"), 3)), 0),
                        11),
                /* 42 */
                new DevelopmentCard(
                        devCardColorFactory.get("Purple"), 3,
                        new ResourceRequirement(Map.ofEntries(
                                entry(resTypeFactory.get("Servant"), 7))),
                        new Production(
                                Map.ofEntries(
                                        entry(resTypeFactory.get("Coin"), 1)),
                                0, Map.ofEntries(
                                        entry(resTypeFactory.get("Stone"), 1),
                                        entry(resTypeFactory.get("Faith"), 3)), 0),
                        11),
                /* 43 */
                new DevelopmentCard(
                        devCardColorFactory.get("Blue"), 3,
                        new ResourceRequirement(Map.ofEntries(
                                entry(resTypeFactory.get("Coin"), 7))),
                        new Production(
                                Map.ofEntries(
                                        entry(resTypeFactory.get("Stone"), 1)),
                                0, Map.ofEntries(
                                        entry(resTypeFactory.get("Shield"), 1),
                                        entry(resTypeFactory.get("Faith"), 3)), 0),
                        11),
                /* 44 */
                new DevelopmentCard(
                        devCardColorFactory.get("Yellow"), 3,
                        new ResourceRequirement(Map.ofEntries(
                                entry(resTypeFactory.get("Stone"), 7))),
                        new Production(
                                Map.ofEntries(
                                        entry(resTypeFactory.get("Shield"), 1)),
                                0, Map.ofEntries(
                                        entry(resTypeFactory.get("Servant"), 1),
                                        entry(resTypeFactory.get("Faith"), 3)), 0),
                        11),
                /* 45 */
                new DevelopmentCard(
                        devCardColorFactory.get("Green"), 3,
                        new ResourceRequirement(Map.ofEntries(
                                entry(resTypeFactory.get("Shield"), 4),
                                entry(resTypeFactory.get("Coin"), 4))),
                        new Production(
                                Map.ofEntries(
                                        entry(resTypeFactory.get("Stone"), 1)),
                                0, Map.ofEntries(
                                        entry(resTypeFactory.get("Coin"), 3),
                                        entry(resTypeFactory.get("Shield"), 1)), 0),
                        12),
                /* 46 */
                new DevelopmentCard(
                        devCardColorFactory.get("Purple"), 3,
                        new ResourceRequirement(Map.ofEntries(
                                entry(resTypeFactory.get("Servant"), 4),
                                entry(resTypeFactory.get("Shield"), 4))),
                        new Production(
                                Map.ofEntries(
                                        entry(resTypeFactory.get("Coin"), 1)),
                                0, Map.ofEntries(
                                        entry(resTypeFactory.get("Stone"), 3),
                                        entry(resTypeFactory.get("Servant"), 1)), 0),
                        12),
                /* 47 */
                new DevelopmentCard(
                        devCardColorFactory.get("Blue"), 3,
                        new ResourceRequirement(Map.ofEntries(
                                entry(resTypeFactory.get("Coin"), 4),
                                entry(resTypeFactory.get("Stone"), 4))),
                        new Production(
                                Map.ofEntries(
                                        entry(resTypeFactory.get("Servant"), 1)),
                                0, Map.ofEntries(
                                        entry(resTypeFactory.get("Coin"), 1),
                                        entry(resTypeFactory.get("Shield"), 3)), 0),
                        12),
                /* 48 */
                new DevelopmentCard(
                        devCardColorFactory.get("Yellow"), 3,
                        new ResourceRequirement(Map.ofEntries(
                                entry(resTypeFactory.get("Stone"), 4),
                                entry(resTypeFactory.get("Servant"), 4))),
                        new Production(
                                Map.ofEntries(
                                        entry(resTypeFactory.get("Shield"), 1)),
                                0, Map.ofEntries(
                                        entry(resTypeFactory.get("Stone"), 1),
                                        entry(resTypeFactory.get("Servant"), 3)), 0),
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
                new DiscountLeader(1, 0, null, resTypeFactory.get("Servant"), new DevCardRequirement(
                        Set.of(
                                new DevCardRequirement.Entry(devCardColorFactory.get("Yellow"), 0, 1),
                                new DevCardRequirement.Entry(devCardColorFactory.get("Green"), 0, 1)
                )), 2),
                /* 50 */
                new DiscountLeader(1, 0, null, resTypeFactory.get("Shield"), new DevCardRequirement(
                        Set.of(
                                new DevCardRequirement.Entry(devCardColorFactory.get("Blue"), 0, 1),
                                new DevCardRequirement.Entry(devCardColorFactory.get("Purple"), 0, 1)
                )), 2),
                /* 51 */
                new DiscountLeader(1, 0, null, resTypeFactory.get("Stone"), new DevCardRequirement(
                        Set.of(
                                new DevCardRequirement.Entry(devCardColorFactory.get("Green"), 0, 1),
                                new DevCardRequirement.Entry(devCardColorFactory.get("Blue"), 0, 1)
                )), 2),
                /* 52 */
                new DiscountLeader(1, 0, null, resTypeFactory.get("Coin"), new DevCardRequirement(
                        Set.of(
                                new DevCardRequirement.Entry(devCardColorFactory.get("Yellow"), 0, 1),
                                new DevCardRequirement.Entry(devCardColorFactory.get("Purple"), 0, 1)
                )), 2),
                /* 53 */
                new DepotLeader(0, 2, null, resTypeFactory.get("Stone"), new ResourceRequirement(Map.ofEntries(
                        entry(resTypeFactory.get("Coin"), 5)
                )), 3),
                /* 54 */
                new DepotLeader(0, 2, null, resTypeFactory.get("Servant"), new ResourceRequirement(Map.ofEntries(
                        entry(resTypeFactory.get("Stone"), 5)
                )), 3),
                /* 55 */
                new DepotLeader(0, 2, null, resTypeFactory.get("Shield"), new ResourceRequirement(Map.ofEntries(
                        entry(resTypeFactory.get("Servant"), 5)
                )), 3),
                /* 56 */
                new DepotLeader(0, 2, null, resTypeFactory.get("Coin"), new ResourceRequirement(Map.ofEntries(
                        entry(resTypeFactory.get("Shield"), 5)
                )), 3),
                /* 57 */
                new ZeroLeader(0, 0, null, resTypeFactory.get("Servant"), new DevCardRequirement(
                        Set.of(
                                new DevCardRequirement.Entry(devCardColorFactory.get("Yellow"), 0, 2),
                                new DevCardRequirement.Entry(devCardColorFactory.get("Blue"), 0, 1)
                )), 5),
                /* 58 */
                new ZeroLeader(0, 0, null, resTypeFactory.get("Shield"), new DevCardRequirement(
                        Set.of(
                                new DevCardRequirement.Entry(devCardColorFactory.get("Green"), 0, 2),
                                new DevCardRequirement.Entry(devCardColorFactory.get("Purple"), 0, 1)
                )), 5),
                /* 59 */
                new ZeroLeader(0, 0, null, resTypeFactory.get("Stone"), new DevCardRequirement(
                        Set.of(
                                new DevCardRequirement.Entry(devCardColorFactory.get("Blue"), 0, 2),
                                new DevCardRequirement.Entry(devCardColorFactory.get("Yellow"), 0, 1)
                )), 5),
                /* 60 */
                new ZeroLeader(0, 0, null, resTypeFactory.get("Coin"), new DevCardRequirement(
                        Set.of(
                                new DevCardRequirement.Entry(devCardColorFactory.get("Purple"), 0, 2),
                                new DevCardRequirement.Entry(devCardColorFactory.get("Green"), 0, 1)
                )), 5),
                /* 61 */
                new ProductionLeader(0, 0,
                        new Production(
                                Map.ofEntries(
                                        entry(resTypeFactory.get("Shield"), 1)),
                                0,
                                Map.ofEntries(
                                        entry(resTypeFactory.get("Faith"), 1)),
                                1),
                        resTypeFactory.get("Servant"), new DevCardRequirement(
                                Set.of(
                                new DevCardRequirement.Entry(devCardColorFactory.get("Yellow"), 2, 1)
                )), 4),
                /* 62 */
                new ProductionLeader(0, 0,
                        new Production(
                                Map.ofEntries(
                                        entry(resTypeFactory.get("Servant"), 1)),
                                0,
                                Map.ofEntries(
                                        entry(resTypeFactory.get("Faith"), 1)),
                                1),
                        resTypeFactory.get("Servant"), new DevCardRequirement(
                                Set.of(
                                        new DevCardRequirement.Entry(devCardColorFactory.get("Blue"), 2, 1)
                )), 4),
                /* 63 */
                new ProductionLeader(0, 0,
                        new Production(
                                Map.ofEntries(
                                        entry(resTypeFactory.get("Stone"), 1)),
                                0,
                                Map.ofEntries(
                                        entry(resTypeFactory.get("Faith"), 1)),
                                1),
                        resTypeFactory.get("Servant"), new DevCardRequirement(
                                Set.of(
                                new DevCardRequirement.Entry(devCardColorFactory.get("Purple"), 2, 1)
                )), 4),
                /* 64 */
                new ProductionLeader(0, 0,
                        new Production(
                                Map.ofEntries(
                                        entry(resTypeFactory.get("Coin"), 1)),
                                0,
                                Map.ofEntries(
                                        entry(resTypeFactory.get("Faith"), 1)),
                                1),
                        resTypeFactory.get("Servant"), new DevCardRequirement(
                                Set.of(
                                new DevCardRequirement.Entry(devCardColorFactory.get("Green"), 2, 1)
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
                entry(resTypeFactory.get("Coin"), 2),
                entry(resTypeFactory.get("Faith"), 1),
                entry(resTypeFactory.get("Servant"), 2),
                entry(resTypeFactory.get("Shield"), 2),
                entry(resTypeFactory.get("Stone"), 2),
                entry(resTypeFactory.get("Zero"), 4)
        );
    }

    /**
     * Returns a set of the vatican sections.
     *
     * @return  set of the vatican sections
     */
    public Set<FaithTrack.VaticanSection> generateVaticanSections() {
        return Set.of(
                new FaithTrack.VaticanSection(5, 8, 2),
                new FaithTrack.VaticanSection(12, 16, 3),
                new FaithTrack.VaticanSection(19, 24, 4)
        );
    }

    /**
     * Returns a set of the yellow tiles.
     *
     * @return  set of the yellow tiles
     */
    public Set<FaithTrack.YellowTile> generateYellowTiles() {
        return Set.of(
                new FaithTrack.YellowTile(3, 1),
                new FaithTrack.YellowTile(6, 2),
                new FaithTrack.YellowTile(9, 4),
                new FaithTrack.YellowTile(12, 6),
                new FaithTrack.YellowTile(15, 9),
                new FaithTrack.YellowTile(18, 12),
                new FaithTrack.YellowTile(21, 16),
                new FaithTrack.YellowTile(24, 20)
        );
    }

    /**
     * Returns a list of the action tokens.
     *
     * @return  list of action tokens
     */
    public List<ActionToken> generateActionTokens() {
        return List.of(
                new ActionTokenBlackMoveTwo(),
                new ActionTokenBlackMoveTwo(),
                new ActionTokenBlackMoveOneShuffle(),
                new ActionTokenDiscardTwo(devCardColorFactory.get("Blue")),
                new ActionTokenDiscardTwo(devCardColorFactory.get("Green")),
                new ActionTokenDiscardTwo(devCardColorFactory.get("Purple")),
                new ActionTokenDiscardTwo(devCardColorFactory.get("Yellow"))
        );
    }
}
