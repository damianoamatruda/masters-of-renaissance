package it.polimi.ingsw.model;

import it.polimi.ingsw.model.resourcetypes.ResourceType;

import java.util.List;

/**
 * This interface represents a factory of game instances.
 */
public interface GameFactory {
    /**
     * Builder of a multiplayer game instance.
     *
     * @param nicknames the list of nicknames of players who joined
     * @return          the multiplayer game
     */
    Game getMultiGame(List<String> nicknames);

    /**
     * Builder of a single-player game instance.
     *
     * @param nickname  the nickname of the only player
     * @return          the single-player game
     */
    SoloGame getSoloGame(String nickname);

    /**
     * Getter of a resource type in the game by its name.
     *
     * @return  the resource type
     */
    @Deprecated
    ResourceType getResourceType(String name);

    /**
     * Getter of a development card color in the game by its name.
     *
     * @return  the development card color
     */
    @Deprecated
    DevCardColor getDevCardColor(String name);
}
