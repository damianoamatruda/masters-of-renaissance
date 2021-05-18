package it.polimi.ingsw.common.events.mvevents.errors;

import it.polimi.ingsw.common.events.mvevents.MVEvent;

public enum ErrNickname implements MVEvent {
    NOTSET,
    ALREADYSET,
    TAKEN,
    NOTINGAME
}
