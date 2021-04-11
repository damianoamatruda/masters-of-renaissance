package it.polimi.ingsw;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.GameFactory;
import it.polimi.ingsw.model.Player;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The main class of the project.
 */
public class App {
    /**
     * Entry point of the main program.
     *
     * @param args  the command line arguments
     */
    public static void main(String[] args) {
        GameFactory gameFactory = new FileGameFactory("src/main/resources/config.xml");
        Game game = gameFactory.buildMultiGame(List.of("PlayerA", "PlayerB", "PlayerC"));

        System.out.println("Players: " + game.getPlayers().stream()
                .map(Player::getNickname).collect(Collectors.joining(", ")));
    }
}
