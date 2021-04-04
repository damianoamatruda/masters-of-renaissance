package it.polimi.ingsw;

import it.polimi.ingsw.devcardcolors.Blue;
import it.polimi.ingsw.devcardcolors.Green;
import it.polimi.ingsw.devcardcolors.Purple;
import it.polimi.ingsw.devcardcolors.Yellow;
import it.polimi.ingsw.leadercards.*;
import it.polimi.ingsw.resourcetypes.*;

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
        Game game = new OriginalGame(List.of("Player1", "Player2", "Player3"));

        System.out.println("Players: " + game.getPlayers().stream()
                .map(Player::getNickname).collect(Collectors.joining(", ")));
    }
}
