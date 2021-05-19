package it.polimi.ingsw.common.events.mvevents.errors;

import it.polimi.ingsw.common.events.mvevents.MVEvent;

public class ErrNickname implements MVEvent {
    private final ErrNicknameReason reason;
    
    /**
     * @param reason
     */
    public ErrNickname(ErrNicknameReason reason) {
        this.reason = reason;
    }

    public ErrNicknameReason getReason() {
        return reason;
    }

    public enum ErrNicknameReason {
        NOTSET,
        ALREADYSET,
        TAKEN,
        NOTINGAME
    }
}
