package it.polimi.ingsw.server.model;

import java.util.Stack;

/**
 * Exception thrown when a player wants to deposit a development card on the wrong slot.
 */
public class IllegalCardDepositException extends Exception {

    public IllegalCardDepositException(DevelopmentCard card, Stack<DevelopmentCard> slot, int slotIndex) {
        super(
            String.format("Cannot place development card in slot %d: card level %d, slot's top card level %d",
                slotIndex, card.getLevel(), slot.empty() ? 0 : slot.peek().getLevel()));
    }
}
