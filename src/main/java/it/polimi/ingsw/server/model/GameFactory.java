package it.polimi.ingsw.server.model;

import java.util.List;

/**
 * This interface represents a factory of game instances.
 */
public interface GameFactory {
    /**
     * Builder of a multiplayer game instance.
     *
     * @param nicknames the list of nicknames of players who joined
     * @return the multiplayer game
     */
    Game getMultiGame(List<String> nicknames);

    /**
     * Builder of a single-player game instance.
     *
     * @param nickname the nickname of the only player
     * @return the single-player game
     */
    SoloGame getSoloGame(String nickname);
}