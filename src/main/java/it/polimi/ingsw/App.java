package it.polimi.ingsw;

import java.util.ArrayList;

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
        Game game = new Game(null,null);
        game = new SoloGame(game, new ArrayList<>());
        game = new SoloGame(game, new ArrayList<>());

        System.out.println("Hello, world!");
    }
}
