package it.polimi.ingsw.common.events.mvevents;

import java.util.List;

/** Development card slot state update. */
public class UpdateDevSlot implements MVEvent {
    /** The ID of the slot the cards were added to. */
    private final int devSlot;

    /** The IDs of the card added to the slot. */
    private final List<Integer> devCards;

    /**
     * Class constructor.
     *
     * @param devSlot  the ID of the slot the cards were added to
     * @param devCards the IDs of the cards added to the slot
     */
    public UpdateDevSlot(int devSlot, List<Integer> devCards) {
        this.devSlot = devSlot;
        this.devCards = devCards;
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
