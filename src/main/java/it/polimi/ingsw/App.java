package it.polimi.ingsw;

import java.util.List;
import java.util.stream.Collectors;

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
        Game game = new Game(List.of("Player1", "Player2", "Player3"), Game.getLeaderCards(), Game.getDevCards(), 4);
        //game = new SoloGame(game, new ArrayList<>());

        System.out.println("Players: " + game.getPlayers().stream()
                .map(Player::getNickname).collect(Collectors.joining(", ")));
    }
}
