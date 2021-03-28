package it.polimi.ingsw;

import java.lang.reflect.Array;
import java.util.*;

import it.polimi.ingsw.devcardcolors.DevCardColor;

/**
 * Requirement for leader card activation.
 * Specifies what kind of development cards the player must have to be able to activate a leader.
 */
public class DevCardRequirement implements CardRequirement {
    /**
     * The development cards required to activate the leader card.
     */
    private final Map<DevCardColor, int[]> reqCards;

    /**
     * Class constructor.
     *
     * @param devCards the development cards that form the requirement.
     */
    public DevCardRequirement(List<DevelopmentCard> devCards) {
        reqCards = new HashMap<>();

        buildRequirements(devCards, reqCards);
    }

    private void buildRequirements(List<DevelopmentCard> cards, Map<DevCardColor, int[]> requirements) {
        cards.forEach(devCard ->
            requirements.compute(devCard.getColor(), (color, levels) -> {
                if (levels == null) levels = new int[Game.getDevGridLevelsCount()];

                levels[devCard.getLevel()]++;

                return levels;
            })
        );
    }

    @Override
    public void checkRequirements(Player player) throws Exception {
        Map<DevCardColor, int[]> playerCards = new HashMap<>();

        // build map of player's cards for easy comparison
        // using the same data structure as the requirements
        for (int i = 0; i < Player.getDevSlotsCount(); i++)
            buildRequirements(player.getDevSlot(i), playerCards);
        
        // if the playercards don't contain all colors required
        if (!playerCards.keySet().containsAll(reqCards.keySet())) throw new Exception();
        
        // compare each key's levels
        // I would have done it this way, but Java doesn't want me to do things inside forEach
        // reqCards.forEach((color, levels) -> {
        //     if (Arrays.compare(playerCards.get(color), levels) < 0) throw new Exception();
        // });
        // so, here's the dumb way
        ArrayList<DevCardColor> playerColors = new ArrayList<>(playerCards.keySet());

        for (int i = 0; i < playerColors.size(); i++) {
            int[] playerLevels = playerCards.get(playerColors.get(i)),
                  reqLevels = reqCards.get(playerColors.get(i));
            
            if (Arrays.compare(playerLevels, reqLevels) < 0) throw new Exception();
        }
    }

}
