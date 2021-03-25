package it.polimi.ingsw;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Stack;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        Game game = new Game(null,null,null);
        game = new SoloGame(game, new ArrayList<>());
        game = new SoloGame(game, new ArrayList<>());

    }
}
