package it.polimi.ingsw.client.ViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.polimi.ingsw.common.reducedmodel.ReducedDevCard;
import it.polimi.ingsw.common.reducedmodel.ReducedLeaderCard;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceContainer;
import it.polimi.ingsw.common.reducedmodel.ReducedResourceTransactionRecipe;

/** Data storage cache on the Masters Of Renaissance client. */
public class ViewModel {
    /** 
     * Data concerning the UI.
     * 
     * E.g. state-aiding variables, old requests...
     */
    private final UIData uiData;
    /**
     * Game-global objects sent by the server.
     * Object specifications are here. For ownership, check PlayerData
     * 
     * E.g. cards, resources, containers...
     */
    private final GameData gameData;
    /** 
     * Player data.
     * 
     * E.g. ownership of objects, victory points...
     */
    private final Map<String, PlayerData> playerData;

    /**
     * Class constructor.
     * Initializes empty objects.
     */
    public ViewModel() {
        this.uiData = new UIData();
        this.gameData = new GameData();
        this.playerData = new HashMap<>();
    }
    
    /**
     * @return the ui data
     */
    public UIData getUiData() {
        return uiData;
    }
    
    /**
     * @return the game data
     */
    public GameData getGameData() {
        return gameData;
    }
    
    /**
     * @param nickname the nickname of the player whose data needs to be retrieved.
     * @return the playerData of the specified player
     */
    public PlayerData getPlayerData(String nickname) {
        return playerData.get(nickname);
    }

    /**
     * To be used when receiving the first UpdatePlayer message.
     * 
     * @param playerData the player's data
     */
    public void setPlayerData(String nickname, PlayerData playerData) {
        this.playerData.put(nickname, playerData);
    }

    /**
     * @param nickname
     * @return the reduced recipes owned by the player, including:
     *          <ul>
     *              <li>the base production
     *              <li>the development cards productions
     *              <li>the leader cards productions
     */
    public List<ReducedResourceTransactionRecipe> getPlayerProductions(String nickname) {
        PlayerData pd = playerData.get(nickname);
        List<Integer> ids = new ArrayList<>();
        
        ids.add(pd.getBaseProduction());
        getPlayerLeaderCards(nickname).forEach(c -> {
            if (c.getProduction() >= 0)
                ids.add(c.getProduction());
        });
        getPlayerDevelopmentCards(nickname).forEach(c -> {
            if (c.getProduction() >= 0)
                ids.add(c.getProduction());
        });

        return gameData.getProductions().stream().filter(p -> ids.contains(p.getId())).toList();
    }

    /**
     * @param nickname
     * @return the topmost development cards in the player's slots
     */
    private List<ReducedDevCard> getPlayerDevelopmentCards(String nickname) {
        return playerData.get(nickname).getDevSlots().stream().map(slot -> gameData.getDevelopmentCard(slot.get(0))).toList();
    }

    /**
     * @param nickname
     * @return the reduced leader cards owned by the player
     */
    public List<ReducedLeaderCard> getPlayerLeaderCards(String nickname) {
        return playerData.get(nickname).getLeadersHand().stream().map(id -> gameData.getLeaderCard(id)).toList();
    }

    /**
     * @param nickname
     * @return the player's reduced containers, including leader depots
     */
    public List<ReducedResourceContainer> getPlayerShelves(String nickname) {
        List<Integer> ids = new ArrayList<>(playerData.get(nickname).getWarehouseShelves());

        playerData.get(nickname).getLeadersHand().forEach(id -> { if (id >= 0) ids.add(gameData.getLeaderCard(id).getContainerId()); });

        return ids.stream().map(id -> gameData.getContainer(id)).toList();
    }
}
