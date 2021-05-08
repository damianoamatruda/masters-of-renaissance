package it.polimi.ingsw.common.events;

import it.polimi.ingsw.common.View;

public class UpdateDevCardSlot implements MVEvent {
    private final int card, slot;

    public UpdateDevCardSlot(int card, int slot) {
        this.card = card;
        this.slot = slot;
    }

    /**
     * @return the slot
     */
    public int getSlot() {
        return slot;
    }

    /**
     * @return the card
     */
    public int getCard() {
        return card;
    }

    @Override
    public void handle(View view) {
        // TODO Auto-generated method stub
        
    }
}
