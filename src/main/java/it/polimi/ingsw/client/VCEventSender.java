package it.polimi.ingsw.client;

import it.polimi.ingsw.common.events.VCEvent;

public interface VCEventSender {
    void send(VCEvent event);

    void stop();
}