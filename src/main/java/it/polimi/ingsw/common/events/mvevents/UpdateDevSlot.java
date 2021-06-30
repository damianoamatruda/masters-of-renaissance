package it.polimi.ingsw.common.events.mvevents;

import java.util.List;

/** Development card slot state update. */
public class UpdateDevSlot implements MVEvent {
    /** The nickname of the player that has added the development cards. */
    private final String player;

    /** The ID of the slot the cards were added to. */
    private final int devSlot;

    /** The IDs of the card added to the slot. */
    private final List<Integer> devCards;

    /**
     * Class constructor.
     *
     * @param player   the nickname of the player that has added the leader cards
     * @param devSlot  the ID of the slot the cards were added to
     * @param devCards the IDs of the cards added to the slot
     */
    public UpdateDevSlot(String player, int devSlot, List<Integer> devCards) {
        this.player = player;
        this.devSlot = devSlot;
        this.devCards = devCards;
    }

    /**
     * @return the nickname of the player that has added the development cards
     */
    public String getPlayer() {
        return player;
    }

    /**
     * @return the ID of the slot the card was added to
     */
    public int getDevSlot() {
        return devSlot;
    }

    /**
     * @return the IDs of the cards added to the slot
     */
    public List<Integer> getDevCards() {
        return devCards;
    }
}
