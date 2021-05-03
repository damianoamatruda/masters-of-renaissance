package it.polimi.ingsw.server;

import it.polimi.ingsw.common.events.MVEvent;

public interface MVEventSender {
    void send(MVEvent event);

    void stop();
}
