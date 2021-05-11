package it.polimi.ingsw.common.events.mvevents;

import it.polimi.ingsw.common.View;

/** Server response to a request to join the lobby. */
public class ResJoin implements MVEvent {
    /** <code>true</code> if the player joining is the first of the match; <code>false</code> otherwise. */
    private final boolean isFirst;

    /**
     * Class constructor.
     *
     * @param isFirst <code>true</code> if the player joining is the first of the match; <code>false</code> otherwise.
     */
    public ResJoin(boolean isFirst) {
        this.isFirst = isFirst;
    }

    @Override
    public void handle(View view) {
        view.update(this);
    }

    /**
     * @return <code>true</code> if the player joining is the first of the match; <code>false</code> otherwise.
     */
    public boolean isFirst() {
        return isFirst;
    }
}
