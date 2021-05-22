package it.polimi.ingsw.client.ViewModel;

import java.util.HashMap;
import java.util.Map;

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
     * @param playerData
     */
    public void setPlayerData(String nickname, PlayerData playerData) {
        this.playerData.put(nickname, playerData);
    }
}
