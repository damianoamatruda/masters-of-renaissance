package it.polimi.ingsw.model;

import it.polimi.ingsw.model.devcardcolors.Blue;
import it.polimi.ingsw.model.devcardcolors.Green;
import it.polimi.ingsw.model.devcardcolors.Purple;
import it.polimi.ingsw.model.devcardcolors.Yellow;
import it.polimi.ingsw.model.leadercards.*;
import it.polimi.ingsw.model.resourcetypes.*;

import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

/**
 * This class represents a game with the original parameters.
 */
public class OriginalGame extends Game {
    /**
     * Constructor of OriginalGame instances.
     *
     * @param nicknames the list of nicknames of players who joined
     */
    public OriginalGame(List<String> nicknames) {
        super(nicknames,
                getLeaderCards(),
                4,
                getDevCards(),
                3,
                4,
                getMarketResources(),
                4,
                24,
                3,
                3,
                7,
                generateVaticanSections(),
                generateYellowTiles());
    }

    /**
     * Returns the list of all possible development cards.
     *
     * @return  list of development cards
     */
    public static List<DevelopmentCard> getDevCards() {
        return List.of(
                /* 1 */
                new DevelopmentCard(
                        Green.getInstance(), 1,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Shield.getInstance(), 2))),
                        new Production(
                                Map.ofEntries(
                                        entry(Coin.getInstance(), 1)),
                                Map.ofEntries(
                                        entry(Faith.getInstance(), 1))),
                        1),
                /* 2 */
                new DevelopmentCard(
                        Purple.getInstance(), 1,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Servant.getInstance(), 2))),
                        new Production(
                                Map.ofEntries(
                                        entry(Stone.getInstance(), 1)),
                                Map.ofEntries(
                                        entry(Faith.getInstance(), 1))),
                        1),
                /* 3 */
                new DevelopmentCard(
                        Blue.getInstance(), 1,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Coin.getInstance(), 2))),
                        new Production(
                                Map.ofEntries(
                                        entry(Shield.getInstance(), 1)),
                                Map.ofEntries(
                                        entry(Faith.getInstance(), 1))),
                        1),
                /* 4 */
                new DevelopmentCard(
                        Yellow.getInstance(), 1,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Stone.getInstance(), 2))),
                        new Production(
                                Map.ofEntries(
                                        entry(Servant.getInstance(), 1)),
                                Map.ofEntries(
                                        entry(Faith.getInstance(), 1))),
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
                                Map.ofEntries(
                                        entry(Servant.getInstance(), 1))),
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
                                Map.ofEntries(
                                        entry(Shield.getInstance(), 1))),
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
                                Map.ofEntries(
                                        entry(Stone.getInstance(), 1))),
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
                                Map.ofEntries(
                                        entry(Coin.getInstance(), 1))),
                        2),
                /* 9 */
                new DevelopmentCard(
                        Green.getInstance(), 1,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Shield.getInstance(), 3))),
                        new Production(
                                Map.ofEntries(
                                        entry(Servant.getInstance(), 2)),
                                Map.ofEntries(
                                        entry(Coin.getInstance(), 1),
                                        entry(Shield.getInstance(), 1),
                                        entry(Stone.getInstance(), 1))),
                        3),
                /* 10 */
                new DevelopmentCard(
                        Purple.getInstance(), 1,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Servant.getInstance(), 3))),
                        new Production(
                                Map.ofEntries(
                                        entry(Coin.getInstance(), 2)),
                                Map.ofEntries(
                                        entry(Servant.getInstance(), 1),
                                        entry(Shield.getInstance(), 1),
                                        entry(Stone.getInstance(), 1))),
                        3),
                /* 11 */
                new DevelopmentCard(
                        Blue.getInstance(), 1,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Coin.getInstance(), 3))),
                        new Production(
                                Map.ofEntries(
                                        entry(Stone.getInstance(), 2)),
                                Map.ofEntries(
                                        entry(Coin.getInstance(), 1),
                                        entry(Servant.getInstance(), 1),
                                        entry(Stone.getInstance(), 1))),
                        3),
                /* 12 */
                new DevelopmentCard(
                        Yellow.getInstance(), 1,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Stone.getInstance(), 3))),
                        new Production(
                                Map.ofEntries(
                                        entry(Shield.getInstance(), 2)),
                                Map.ofEntries(
                                        entry(Coin.getInstance(), 1),
                                        entry(Servant.getInstance(), 1),
                                        entry(Stone.getInstance(), 1))),
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
                                Map.ofEntries(
                                        entry(Coin.getInstance(), 2),
                                        entry(Faith.getInstance(), 1))),
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
                                Map.ofEntries(
                                        entry(Stone.getInstance(), 2),
                                        entry(Faith.getInstance(), 1))),
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
                                Map.ofEntries(
                                        entry(Servant.getInstance(), 2),
                                        entry(Faith.getInstance(), 1))),
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
                                Map.ofEntries(
                                        entry(Shield.getInstance(), 2),
                                        entry(Faith.getInstance(), 1))),
                        4),
                /* 17 */
                new DevelopmentCard(
                        Green.getInstance(), 2,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Shield.getInstance(), 4))),
                        new Production(
                                Map.ofEntries(
                                        entry(Stone.getInstance(), 1)),
                                Map.ofEntries(
                                        entry(Faith.getInstance(), 2))),
                        5),
                /* 18 */
                new DevelopmentCard(
                        Purple.getInstance(), 2,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Servant.getInstance(), 4))),
                        new Production(
                                Map.ofEntries(
                                        entry(Coin.getInstance(), 1)),
                                Map.ofEntries(
                                        entry(Faith.getInstance(), 2))),
                        5),
                /* 19 */
                new DevelopmentCard(
                        Blue.getInstance(), 2,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Coin.getInstance(), 4))),
                        new Production(
                                Map.ofEntries(
                                        entry(Servant.getInstance(), 1)),
                                Map.ofEntries(
                                        entry(Faith.getInstance(), 2))),
                        5),
                /* 20 */
                new DevelopmentCard(
                        Yellow.getInstance(), 2,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Stone.getInstance(), 4))),
                        new Production(
                                Map.ofEntries(
                                        entry(Shield.getInstance(), 1)),
                                Map.ofEntries(
                                        entry(Faith.getInstance(), 2))),
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
                                Map.ofEntries(
                                        entry(Stone.getInstance(), 3))),
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
                                Map.ofEntries(
                                        entry(Shield.getInstance(), 3))),
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
                                Map.ofEntries(
                                        entry(Servant.getInstance(), 3))),
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
                                Map.ofEntries(
                                        entry(Coin.getInstance(), 3))),
                        6),
                /* 25 */
                new DevelopmentCard(
                        Green.getInstance(), 2,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Shield.getInstance(), 5))),
                        new Production(
                                Map.ofEntries(
                                        entry(Coin.getInstance(), 2)),
                                Map.ofEntries(
                                        entry(Stone.getInstance(), 2),
                                        entry(Faith.getInstance(), 2))),
                        7),
                /* 26 */
                new DevelopmentCard(
                        Purple.getInstance(), 2,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Servant.getInstance(), 5))),
                        new Production(
                                Map.ofEntries(
                                        entry(Stone.getInstance(), 1)),
                                Map.ofEntries(
                                        entry(Coin.getInstance(), 2),
                                        entry(Faith.getInstance(), 2))),
                        7),
                /* 27 */
                new DevelopmentCard(
                        Blue.getInstance(), 2,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Coin.getInstance(), 5))),
                        new Production(
                                Map.ofEntries(
                                        entry(Servant.getInstance(), 2)),
                                Map.ofEntries(
                                        entry(Shield.getInstance(), 2),
                                        entry(Faith.getInstance(), 2))),
                        7),
                /* 28 */
                new DevelopmentCard(
                        Yellow.getInstance(), 2,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Stone.getInstance(), 5))),
                        new Production(
                                Map.ofEntries(
                                        entry(Shield.getInstance(), 2)),
                                Map.ofEntries(
                                        entry(Servant.getInstance(), 2),
                                        entry(Faith.getInstance(), 2))),
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
                                Map.ofEntries(
                                        entry(Shield.getInstance(), 2),
                                        entry(Faith.getInstance(), 1))),
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
                                Map.ofEntries(
                                        entry(Servant.getInstance(), 2),
                                        entry(Faith.getInstance(), 1))),
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
                                Map.ofEntries(
                                        entry(Stone.getInstance(), 2),
                                        entry(Faith.getInstance(), 1))),
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
                                Map.ofEntries(
                                        entry(Coin.getInstance(), 2),
                                        entry(Faith.getInstance(), 1))),
                        8),
                /* 33 */
                new DevelopmentCard(
                        Green.getInstance(), 3,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Shield.getInstance(), 6))),
                        new Production(
                                Map.ofEntries(
                                        entry(Coin.getInstance(), 2)),
                                Map.ofEntries(
                                        entry(Stone.getInstance(), 3),
                                        entry(Faith.getInstance(), 2))),
                        9),
                /* 34 */
                new DevelopmentCard(
                        Purple.getInstance(), 3,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Servant.getInstance(), 6))),
                        new Production(
                                Map.ofEntries(
                                        entry(Stone.getInstance(), 2)),
                                Map.ofEntries(
                                        entry(Coin.getInstance(), 3),
                                        entry(Faith.getInstance(), 2))),
                        9),
                /* 35 */
                new DevelopmentCard(
                        Blue.getInstance(), 3,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Coin.getInstance(), 6))),
                        new Production(
                                Map.ofEntries(
                                        entry(Servant.getInstance(), 2)),
                                Map.ofEntries(
                                        entry(Shield.getInstance(), 3),
                                        entry(Faith.getInstance(), 2))),
                        9),
                /* 36 */
                new DevelopmentCard(
                        Yellow.getInstance(), 3,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Stone.getInstance(), 6))),
                        new Production(
                                Map.ofEntries(
                                        entry(Shield.getInstance(), 2)),
                                Map.ofEntries(
                                        entry(Servant.getInstance(), 3),
                                        entry(Faith.getInstance(), 2))),
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
                                Map.ofEntries(
                                        entry(Shield.getInstance(), 2),
                                        entry(Stone.getInstance(), 2),
                                        entry(Faith.getInstance(), 1))),
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
                                Map.ofEntries(
                                        entry(Coin.getInstance(), 2),
                                        entry(Servant.getInstance(), 2),
                                        entry(Faith.getInstance(), 1))),
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
                                Map.ofEntries(
                                        entry(Servant.getInstance(), 2),
                                        entry(Stone.getInstance(), 2),
                                        entry(Faith.getInstance(), 1))),
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
                                Map.ofEntries(
                                        entry(Coin.getInstance(), 2),
                                        entry(Shield.getInstance(), 2),
                                        entry(Faith.getInstance(), 1))),
                        10),
                /* 41 */
                new DevelopmentCard(
                        Green.getInstance(), 3,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Shield.getInstance(), 7))),
                        new Production(
                                Map.ofEntries(
                                        entry(Servant.getInstance(), 1)),
                                Map.ofEntries(
                                        entry(Coin.getInstance(), 1),
                                        entry(Faith.getInstance(), 3))),
                        11),
                /* 42 */
                new DevelopmentCard(
                        Purple.getInstance(), 3,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Servant.getInstance(), 7))),
                        new Production(
                                Map.ofEntries(
                                        entry(Coin.getInstance(), 1)),
                                Map.ofEntries(
                                        entry(Stone.getInstance(), 1),
                                        entry(Faith.getInstance(), 3))),
                        11),
                /* 43 */
                new DevelopmentCard(
                        Blue.getInstance(), 3,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Coin.getInstance(), 7))),
                        new Production(
                                Map.ofEntries(
                                        entry(Stone.getInstance(), 1)),
                                Map.ofEntries(
                                        entry(Shield.getInstance(), 1),
                                        entry(Faith.getInstance(), 3))),
                        11),
                /* 44 */
                new DevelopmentCard(
                        Yellow.getInstance(), 3,
                        new ResourceRequirement(Map.ofEntries(
                                entry(Stone.getInstance(), 7))),
                        new Production(
                                Map.ofEntries(
                                        entry(Shield.getInstance(), 1)),
                                Map.ofEntries(
                                        entry(Servant.getInstance(), 1),
                                        entry(Faith.getInstance(), 3))),
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
                                Map.ofEntries(
                                        entry(Coin.getInstance(), 3),
                                        entry(Shield.getInstance(), 1))),
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
                                Map.ofEntries(
                                        entry(Stone.getInstance(), 3),
                                        entry(Servant.getInstance(), 1))),
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
                                Map.ofEntries(
                                        entry(Coin.getInstance(), 1),
                                        entry(Shield.getInstance(), 3))),
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
                                Map.ofEntries(
                                        entry(Stone.getInstance(), 1),
                                        entry(Servant.getInstance(), 3))),
                        12)
        );
    }

    /**
     * Returns the list of all possible leader cards.
     *
     * @return  list of leader cards
     */
    public static List<LeaderCard> getLeaderCards() {
        return List.of(
                /* 49 */
                new DiscountLeader(1, Servant.getInstance(), new DevCardRequirement(Map.ofEntries(
                        entry(Map.ofEntries(
                                entry(Yellow.getInstance(), 0),
                                entry(Green.getInstance(), 0)
                        ), 1)
                )), 2),
                /* 50 */
                new DiscountLeader(1, Shield.getInstance(), new DevCardRequirement(Map.ofEntries(
                        entry(Map.ofEntries(
                                entry(Blue.getInstance(), 0),
                                entry(Purple.getInstance(), 0)
                        ), 1)
                )), 2),
                /* 51 */
                new DiscountLeader(1, Stone.getInstance(), new DevCardRequirement(Map.ofEntries(
                        entry(Map.ofEntries(
                                entry(Green.getInstance(), 0),
                                entry(Blue.getInstance(), 0)
                        ), 1)
                )), 2),
                /* 52 */
                new DiscountLeader(1, Coin.getInstance(), new DevCardRequirement(Map.ofEntries(
                        entry(Map.ofEntries(
                                entry(Yellow.getInstance(), 0),
                                entry(Purple.getInstance(), 0)
                        ), 1)
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
                new ZeroLeader(Servant.getInstance(), new DevCardRequirement(Map.ofEntries(
                        entry(Map.ofEntries(
                                entry(Yellow.getInstance(), 0)
                        ), 2),
                        entry(Map.ofEntries(
                                entry(Blue.getInstance(), 0)
                        ), 1)
                )), 5),
                /* 58 */
                new ZeroLeader(Shield.getInstance(), new DevCardRequirement(Map.ofEntries(
                        entry(Map.ofEntries(
                                entry(Green.getInstance(), 0)
                        ), 2),
                        entry(Map.ofEntries(
                                entry(Purple.getInstance(), 0)
                        ), 1)
                )), 5),
                /* 59 */
                new ZeroLeader(Stone.getInstance(), new DevCardRequirement(Map.ofEntries(
                        entry(Map.ofEntries(
                                entry(Blue.getInstance(), 0)
                        ), 2),
                        entry(Map.ofEntries(
                                entry(Yellow.getInstance(), 0)
                        ), 1)
                )), 5),
                /* 60 */
                new ZeroLeader(Coin.getInstance(), new DevCardRequirement(Map.ofEntries(
                        entry(Map.ofEntries(
                                entry(Purple.getInstance(), 0)
                        ), 2),
                        entry(Map.ofEntries(
                                entry(Green.getInstance(), 0)
                        ), 1)
                )), 5),
                /* 61 */
                new ProductionLeader(
                        new Production(
                                Map.ofEntries(
                                        entry(Shield.getInstance(), 1)),
                                Map.ofEntries(
                                        entry(Zero.getInstance(), 1),
                                        entry(Faith.getInstance(), 1))),
                        Servant.getInstance(), new DevCardRequirement(Map.ofEntries(
                        entry(Map.ofEntries(
                                entry(Yellow.getInstance(), 2)
                        ), 1)
                )), 4),
                /* 62 */
                new ProductionLeader(
                        new Production(
                                Map.ofEntries(
                                        entry(Servant.getInstance(), 1)),
                                Map.ofEntries(
                                        entry(Zero.getInstance(), 1),
                                        entry(Faith.getInstance(), 1))),
                        Servant.getInstance(), new DevCardRequirement(Map.ofEntries(
                        entry(Map.ofEntries(
                                entry(Blue.getInstance(), 2)
                        ), 1)
                )), 4),
                /* 63 */
                new ProductionLeader(
                        new Production(
                                Map.ofEntries(
                                        entry(Stone.getInstance(), 1)),
                                Map.ofEntries(
                                        entry(Zero.getInstance(), 1),
                                        entry(Faith.getInstance(), 1))),
                        Servant.getInstance(), new DevCardRequirement(Map.ofEntries(
                        entry(Map.ofEntries(
                                entry(Purple.getInstance(), 2)
                        ), 1)
                )), 4),
                /* 64 */
                new ProductionLeader(
                        new Production(
                                Map.ofEntries(
                                        entry(Coin.getInstance(), 1)),
                                Map.ofEntries(
                                        entry(Zero.getInstance(), 1),
                                        entry(Faith.getInstance(), 1))),
                        Servant.getInstance(), new DevCardRequirement(Map.ofEntries(
                        entry(Map.ofEntries(
                                entry(Green.getInstance(), 2)
                        ), 1)
                )), 4)
        );
    }

    /**
     * Returns a map of the resources inside the market.
     *
     * @return  map of the resources to put inside the market
     */
    private static Map<ResourceType, Integer> getMarketResources() {
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
    public static Map<Integer, Integer[]> generateVaticanSections(){
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
    public static Map<Integer, Integer> generateYellowTiles(){
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

}
