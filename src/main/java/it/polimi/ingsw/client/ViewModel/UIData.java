package it.polimi.ingsw.client.ViewModel;

import java.util.ArrayList;
import java.util.List;

import it.polimi.ingsw.common.events.vcevents.VCEvent;

public class UIData {
    private final List<VCEvent> requests;
    private String localPlayerNickname;
    private boolean canPrepareNewGame;

    public UIData() {
        requests = new ArrayList<>();
    }

    /**
     * @return whether the local player can prepare a new game
     */
    public boolean canPrepareNewGame() {
        return canPrepareNewGame;
    }

    /**
     * @param canPrepareNewGame whether the local player can prepare a new game
     */
    public void setCanPrepareNewGame(boolean canPrepareNewGame) {
        this.canPrepareNewGame = canPrepareNewGame;
    }

    /**
     * @return the local player's nickname
     */
    public String getLocalPlayerNickname() {
        return localPlayerNickname;
    }

    /**
     * @param localPlayerNickname the nickname to set
     */
    public void setLocalPlayerNickname(String localPlayerNickname) {
        this.localPlayerNickname = localPlayerNickname;
    }

    /**
     * @return the requests sent by the client
     */
    public List<VCEvent> getRequests() {
        return requests;
    }

    /**
     * @return the last request made to the server
     */
    public VCEvent lastRequest() {
        return requests.get(requests.size() - 1);
    }


}
