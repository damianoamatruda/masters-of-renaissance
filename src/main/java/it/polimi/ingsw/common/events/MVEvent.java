package it.polimi.ingsw.common.events;

import it.polimi.ingsw.common.View;

public interface MVEvent {
    void handle(View view);
}
