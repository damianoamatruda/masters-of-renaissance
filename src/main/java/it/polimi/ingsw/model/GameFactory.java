package it.polimi.ingsw.model;

import it.polimi.ingsw.model.actiontokens.ActionToken;
import it.polimi.ingsw.model.leadercards.LeaderCard;
import it.polimi.ingsw.model.resourcetypes.ResourceType;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Set;

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
    Game buildMultiGame(List<String> nicknames);

    /**
     * Builder of a single-player game instance.
     *
     * @param nickname  the nickname of the only player
     * @return          the single-player game
     */
    SoloGame buildSoloGame(String nickname);

    /**
     * Getter of a resource type in the game by its name.
     *
     * @return  the resource type
     */
    @Deprecated
    ResourceType getResType(String name);

    /**
     * Getter of a development card color in the game by its name.
     *
     * @return  the development card color
     */
    @Deprecated
    DevCardColor getDevCardColor(String name);

    /**
     * Returns a list of all possible development cards.
     *
     * @return  list of development cards
     */
    @Deprecated
    List<DevelopmentCard> generateDevCards();

    /**
     * Returns a set of the vatican sections.
     *
     * @return  set of the vatican sections
     */
    @Deprecated
    Set<FaithTrack.VaticanSection> generateVaticanSections();

    /**
     * Returns a set of the yellow tiles.
     *
     * @return  set of the yellow tiles
     */
    @Deprecated
    Set<FaithTrack.YellowTile> generateYellowTiles();

    /**
     * Returns the maximum level a development card can have.
     *
     * @return  max card level
     */
    @Deprecated
    int parseLevelsCount();
}
