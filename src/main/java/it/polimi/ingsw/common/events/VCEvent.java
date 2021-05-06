package it.polimi.ingsw.common.events;

import it.polimi.ingsw.common.View;

public interface VCEvent extends Event {
    void handle(View view);
}
