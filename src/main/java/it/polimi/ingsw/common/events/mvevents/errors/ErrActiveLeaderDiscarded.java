package it.polimi.ingsw.common.events.mvevents.errors;

import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.events.mvevents.ViewEvent;

public class ErrActiveLeaderDiscarded extends ViewEvent {
    public ErrActiveLeaderDiscarded(View view) {
        super(view);
    }
}
