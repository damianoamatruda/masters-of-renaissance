package it.polimi.ingsw.server.model;

import it.polimi.ingsw.common.View;
import it.polimi.ingsw.server.model.resourcetypes.ResourceType;

import java.util.List;
import java.util.Optional;

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
    Game getMultiGame(List<String> nicknames, List<View> observers);

    /**
     * Builder of a single-player game instance.
     *
     * @param nickname the nickname of the only player
     * @return the single-player game
     */
    SoloGame getSoloGame(String nickname, View observer);

    Optional<ResourceType> getResourceType(String name);

    Optional<DevCardColor> getDevCardColor(String name);
}
