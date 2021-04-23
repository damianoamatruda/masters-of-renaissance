package it.polimi.ingsw;

import it.polimi.ingsw.server.controller.FileGameFactory;
import it.polimi.ingsw.server.model.Game;
import it.polimi.ingsw.server.model.GameFactory;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.gamecontext.GameContext;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The main class of the project.
 */
public class App {
    /**
     * Entry point of the main program.
     *
     * @param args the command line arguments
     */
    @SuppressWarnings("unused")
    public static void main(String[] args) {
        GameFactory gameFactory = new FileGameFactory(App.class.getResourceAsStream("/config.json"));
        Game game = gameFactory.getMultiGame(List.of("PlayerA", "PlayerB", "PlayerC"));
        GameContext gameContext = new GameContext(game);

        System.out.println("Players: " + game.getPlayers().stream()
                .map(Player::getNickname).collect(Collectors.joining(", ")));
    }
}
