package it.polimi.ingsw.common.events.mvevents.errors;

import it.polimi.ingsw.common.View;
import it.polimi.ingsw.common.events.mvevents.ViewEvent;

/** Event signaling an error related to a player's nickname request. */
public class ErrNickname extends ViewEvent {
    private final ErrNicknameReason reason;

    public enum ErrNicknameReason {
        NOT_SET,
        ALREADY_SET,
        TAKEN,
        NOT_IN_GAME
    }

    /**
     * @param view
     * @param reason reason causing the error
     */
    public ErrNickname(View view, ErrNicknameReason reason) {
        super(view);
        this.reason = reason;
    }

    public ErrNicknameReason getReason() {
        return reason;
    }
}
