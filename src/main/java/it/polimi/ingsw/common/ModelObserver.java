package it.polimi.ingsw.common;

import it.polimi.ingsw.common.events.*;

public interface ModelObserver {
    void update(ResGoodbye event);

    void update(MVEvent event);
}
