package it.polimi.ingsw.common.backend.model;

import it.polimi.ingsw.common.backend.model.resourcetypes.ResourceType;

import java.util.List;
import java.util.Optional;

/**
 * This interface represents a factory of game instances.
 */
public interface GameFactory {
    /**
     * Builder of a multiplayer game instance.
     *
     * @param nicknames the list of nicknames of the players who joined. Cannot be null, 0, 1 or greater than the
     *                  maxPlayers config parameter.
     * @return the multiplayer game
     */
    Game getMultiGame(List<String> nicknames);

    /**
     * Builder of a single-player game instance.
     *
     * @param nickname the nickname of the game's only player
     * @return the single-player game
     */
    SoloGame getSoloGame(String nickname);

    int getMaxPlayersCount();

    Optional<ResourceType> getResourceType(String name);

    Optional<DevCardColor> getDevCardColor(String name);
}
