package it.polimi.ingsw;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.OriginalGame;
import it.polimi.ingsw.model.Player;

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
        Game game = new OriginalGame(List.of("PlayerA", "PlayerB", "PlayerC"));

        System.out.println("Players: " + game.getPlayers().stream()
                .map(Player::getNickname).collect(Collectors.joining(", ")));
    }
}
