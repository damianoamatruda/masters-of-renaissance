package it.polimi.ingsw.common.events.mvevents.errors;

import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.events.mvevents.ViewEvent;

public class ErrNickname extends ViewEvent {
    private final ErrNicknameReason reason;

    /**
     * @param view
     * @param reason
     */
    public ErrNickname(View view, ErrNicknameReason reason) {
        super(view);
        this.reason = reason;
    }

    public ErrNicknameReason getReason() {
        return reason;
    }

    public enum ErrNicknameReason {
        NOT_SET,
        ALREADY_SET,
        TAKEN,
        NOT_IN_GAME
    }
}
