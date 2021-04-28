package it.polimi.ingsw;

import it.polimi.ingsw.server.model.FileGameFactory;
import it.polimi.ingsw.server.model.Game;
import it.polimi.ingsw.server.model.GameFactory;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.gamecontext.GameContext;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
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

        List<String> nicknames = List.of("PlayerA", "PlayerB", "PlayerC");

        Game game = gameFactory.getMultiGame(nicknames);

        Map<String, Player> playerMap = nicknames.stream()
                .collect(Collectors.toUnmodifiableMap(
                        Function.identity(),
                        n -> game.getPlayers().stream().filter(p -> p.getNickname().equals(n)).findAny().orElseThrow()));

        GameContext gameContext = new GameContext(game);

        System.out.println("Players: " + game.getPlayers().stream()
                .map(Player::getNickname).collect(Collectors.joining(", ")));
    }
}
