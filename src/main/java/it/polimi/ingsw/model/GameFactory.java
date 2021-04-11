package it.polimi.ingsw.model;

import it.polimi.ingsw.model.actiontokens.ActionToken;
import it.polimi.ingsw.model.devcardcolors.DevCardColorFactory;
import it.polimi.ingsw.model.leadercards.LeaderCard;
import it.polimi.ingsw.model.resourcetypes.ResourceTypeFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Set;

public interface GameFactory {
    Game buildMultiGame(List<String> nicknames);
    SoloGame buildSoloGame(String nickname);

    /**
     * Getter of the factory of resources.
     *
     * @return the factory of resources.
     */
    @Deprecated
    ResourceTypeFactory getResTypeFactory();

    /**
     * Getter of the factory of development colors.
     *
     * @return the factory of development colors.
     */
    @Deprecated
    DevCardColorFactory getDevCardColorFactory();

    /**
     * Returns a list of all possible development cards.
     *
     * @return  list of development cards
     */
    @Deprecated
    List<DevelopmentCard> generateDevCards();

    /**
     * Returns a list of all possible leader cards.
     *
     * @return  list of leader cards
     */
    @Deprecated
    List<LeaderCard> generateLeaderCards() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException;

    /**
     * Returns a new Market instance.
     *
     * @return  the Market
     */
    @Deprecated
    Market generateMarket();

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
     * Returns a list of the action tokens.
     *
     * @return  list of action tokens
     */
    @Deprecated
    List<ActionToken> generateActionTokens() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException;
}
