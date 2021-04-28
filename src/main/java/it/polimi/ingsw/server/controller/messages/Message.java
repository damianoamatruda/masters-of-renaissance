package it.polimi.ingsw.server.controller.messages;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.gamecontext.GameContext;

@FunctionalInterface
public interface Message {
    void handle(GameContext context, Player player);
}
