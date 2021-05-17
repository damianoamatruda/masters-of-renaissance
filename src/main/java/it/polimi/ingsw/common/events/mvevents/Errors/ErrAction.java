package it.polimi.ingsw.common.events.mvevents.errors;

import it.polimi.ingsw.common.events.mvevents.MVEvent;

/** General action error. */
public enum ErrAction implements MVEvent {
    LATESETUPACTION,
    EARLYMANDATORYACTION,
    LATEMANDATORYACTION,
    EARLYTURNEND,
    ENDEDGAME,
    NOTCURRENTPLAYER
}
