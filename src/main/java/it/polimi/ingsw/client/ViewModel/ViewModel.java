package it.polimi.ingsw.client.ViewModel;

import java.util.Map;

/** Data storage cache on the Masters Of Renaissance client. */
public class ViewModel {
    /** 
     * Data concerning the UI.
     * 
     * E.g. state-aiding variables, old requests...
     */
    private UIData uiData;
    /**
     * Game-global objects sent by the server.
     * Object specifications are here. For ownership, check PlayerData
     * 
     * E.g. cards, resources, containers...
     */
    private GameData gameData;
    /** 
     * Player data.
     * 
     * E.g. ownership of objects, victory points...
     */
    private Map<String, PlayerData> playerData;
    
    /**
     * @return the uiData
     */
    public UIData getUiData() {
        return uiData;
    }
    
    /**
     * @param uiData the uiData to set
     */
    public void setUiData(UIData uiData) {
        this.uiData = uiData;
    }

    /**
     * @return the gameData
     */
    public GameData getGameData() {
        return gameData;
    }
    
    /**
     * @param gameData the gameData to set
     */
    public void setGameData(GameData gameData) {
        this.gameData = gameData;
    }
    
    /**
     * @return the playerData
     */
    public Map<String, PlayerData> getPlayerData() {
        return playerData;
    }
    
    /**
     * @param playerData the playerData to set
     */
    public void setPlayerData(Map<String, PlayerData> playerData) {
        this.playerData = playerData;
    }
}
