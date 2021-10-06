package it.polimi.ingsw.common.events.mvevents;

/**
 * Event meaning the successful completion of a player action. It is fired after all other Update events.
 */
public class UpdateAction implements MVEvent {
    /** Symbolizes the player's action. */
    private final ActionType action;

    /** The player carrying out the action. */
    private final String player;
    
    public UpdateAction(String player, ActionType action) {
        this.player = player;
        this.action = action;
    }

    /**
     * @return the player who carried out the action
     */
    public String getPlayer() {
        return player;
    }

    /**
     * @return the completed action
     */
    public ActionType getAction() {
        return action;
    }

    public enum ActionType {
        CHOOSE_LEADERS,
        CHOOSE_RESOURCES,
        SWAP_SHELVES,
        ACTIVATE_LEADER,
        DISCARD_LEADER,
        TAKE_MARKET_RESOURCES,
        BUY_DEVELOPMENT_CARD,
        ACTIVATE_PRODUCTION,
        END_TURN
    }
}
