package it.polimi.ingsw;

import it.polimi.ingsw.devcardcolors.Blue;
import it.polimi.ingsw.devcardcolors.Green;
import it.polimi.ingsw.devcardcolors.Purple;
import it.polimi.ingsw.devcardcolors.Yellow;
import it.polimi.ingsw.leadercards.*;
import it.polimi.ingsw.resourcetypes.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Map.entry;

/**
 * App class.
 */
public class App {
    /**
     * Main.
     *
     * @param args  the arguments
     */
    public static void main(String[] args) {
        Game game = new Game(
                List.of("Player1", "Player2", "Player3"),
                getLeaderCards(),
                4,
                getDevCards(),
                3,
                4,
                getMarketResources(),
                4);
        //game = new SoloGame(game, new ArrayList<>());

        System.out.println("Players: " + game.getPlayers().stream()
                .map(Player::getNickname).collect(Collectors.joining(", ")));
    }

    /**
     * Returns the list of all possible develompent cards.
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
                                        entry(Faith.getInstance(), 1)),
                                false),
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
                                        entry(Faith.getInstance(), 1)),
                                false),
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
                                        entry(Faith.getInstance(), 1)),
                                false),
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
                                        entry(Faith.getInstance(), 1)),
                                false),
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
                                        entry(Servant.getInstance(), 1)),
                                false),
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
                                        entry(Shield.getInstance(), 1)),
                                false),
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
                                        entry(Stone.getInstance(), 1)),
                                false),
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
                                        entry(Coin.getInstance(), 1)),
                                false),
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
                                        entry(Stone.getInstance(), 1)),
                                false),
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
                                        entry(Stone.getInstance(), 1)),
                                false),
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
                                        entry(Stone.getInstance(), 1)),
                                false),
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
                                        entry(Stone.getInstance(), 1)),
                                false),
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
                                        entry(Faith.getInstance(), 1)),
                                false),
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
                                        entry(Faith.getInstance(), 1)),
                                false),
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
                                        entry(Faith.getInstance(), 1)),
                                false),
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
                                        entry(Faith.getInstance(), 1)),
                                false),
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
                                        entry(Faith.getInstance(), 2)),
                                false),
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
                                        entry(Faith.getInstance(), 2)),
                                false),
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
                                        entry(Faith.getInstance(), 2)),
                                false),
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
                                        entry(Faith.getInstance(), 2)),
                                false),
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
                                        entry(Stone.getInstance(), 3)),
                                false),
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
                                        entry(Shield.getInstance(), 3)),
                                false),
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
                                        entry(Servant.getInstance(), 3)),
                                false),
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
                                        entry(Coin.getInstance(), 3)),
                                false),
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
                                        entry(Faith.getInstance(), 2)),
                                false),
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
                                        entry(Faith.getInstance(), 2)),
                                false),
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
                                        entry(Faith.getInstance(), 2)),
                                false),
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
                                        entry(Faith.getInstance(), 2)),
                                false),
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
                                        entry(Faith.getInstance(), 1)),
                                false),
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
                                        entry(Faith.getInstance(), 1)),
                                false),
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
                                        entry(Faith.getInstance(), 1)),
                                false),
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
                                        entry(Faith.getInstance(), 1)),
                                false),
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
                                        entry(Faith.getInstance(), 2)),
                                false),
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
                                        entry(Faith.getInstance(), 2)),
                                false),
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
                                        entry(Faith.getInstance(), 2)),
                                false),
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
                                        entry(Faith.getInstance(), 2)),
                                false),
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
                                        entry(Faith.getInstance(), 1)),
                                false),
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
                                        entry(Faith.getInstance(), 1)),
                                false),
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
                                        entry(Faith.getInstance(), 1)),
                                false),
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
                                        entry(Faith.getInstance(), 1)),
                                false),
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
                                        entry(Faith.getInstance(), 3)),
                                false),
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
                                        entry(Faith.getInstance(), 3)),
                                false),
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
                                        entry(Faith.getInstance(), 3)),
                                false),
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
                                        entry(Faith.getInstance(), 3)),
                                false),
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
                                        entry(Shield.getInstance(), 1)),
                                false),
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
                                        entry(Servant.getInstance(), 1)),
                                false),
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
                                        entry(Shield.getInstance(), 3)),
                                false),
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
                                        entry(Servant.getInstance(), 3)),
                                false),
                        12)
        );
    }

    /**
     * Returns the list of all possible leader cards.
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
                                        entry(Faith.getInstance(), 1)),
                                false),
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
                                        entry(Faith.getInstance(), 1)),
                                false),
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
                                        entry(Faith.getInstance(), 1)),
                                false),
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
                                        entry(Faith.getInstance(), 1)),
                                false),
                        Servant.getInstance(), new DevCardRequirement(Map.ofEntries(
                        entry(Map.ofEntries(
                                entry(Green.getInstance(), 2)
                        ), 1)
                )), 4)
        );
    }

    /**
     * Returns a map of the resources inside the market
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
}
