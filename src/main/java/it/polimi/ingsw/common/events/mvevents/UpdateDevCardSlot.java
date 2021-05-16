package it.polimi.ingsw.common.events.mvevents;

/** Development card slot state update. */
public class UpdateDevCardSlot implements MVEvent {
    /** The ID of the card added to the slot. */
    private final int devCard;

    /** The ID of the slot the card was added to. */
    private final int devSlot;

    /**
     * Class constructor.
     *
     * @param devCard the ID of the card added to the slot
     * @param devSlot the ID of the slot the card was added to
     */
    public UpdateDevCardSlot(int devCard, int devSlot) {
        this.devCard = devCard;
        this.devSlot = devSlot;
    }

    /**
     * @return the ID of the slot the card was added to
     */
    public int getDevSlot() {
        return devSlot;
    }

    /**
     * @return the ID of the card added to the slot
     */
    public int getDevCard() {
        return devCard;
    }
}
