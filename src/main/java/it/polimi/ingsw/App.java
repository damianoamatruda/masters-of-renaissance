package it.polimi.ingsw;

import java.util.ArrayList;
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
        Game game = new Game(new ArrayList<String>(){{
            add("Player1");
            add("Player2");
            add("Player3");
        }});
        //game = new SoloGame(game, new ArrayList<>());

        System.out.println("Players: " + game.getPlayers().stream()
                .map(Player::getNickname).collect(Collectors.joining(", ")));
    }
}
