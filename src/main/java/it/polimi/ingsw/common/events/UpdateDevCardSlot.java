package it.polimi.ingsw.common.events;

import it.polimi.ingsw.common.View;

/** Development card slot state update. */
public class UpdateDevCardSlot implements MVEvent {
    /** The ID of the card added to the slot. */
    private final int card;
    /** The ID of the slot the card was added to. */
    private final int slot;

    /**
     * Class constructor.
     * 
     * @param card the ID of the card added to the slot
     * @param slot the ID of the slot the card was added to
     */
    public UpdateDevCardSlot(int card, int slot) {
        this.card = card;
        this.slot = slot;
    }

    /**
     * @return the ID of the slot the card was added to
     */
    public int getSlot() {
        return slot;
    }

    /**
     * @return the ID of the card added to the slot
     */
    public int getCard() {
        return card;
    }

    @Override
    public void handle(View view) {
        // TODO Auto-generated method stub
        
    }
}
