package it.polimi.ingsw.common.events.mvevents;

import it.polimi.ingsw.common.View;

/** Server response to a request to join the lobby. */
public class ResJoin implements MVEvent {
    /** Whether the player joining is the first of the match. */
    private final boolean isFirst;

    /**
     * Class constructor.
     * 
     * @param isFirst whether the player joining is the first of the match
     */
    public ResJoin(boolean isFirst) {
        this.isFirst = isFirst;
    }

    @Override
    public void handle(View view) {
        view.update(this);
    }

    /**
     * @return whether the player joining is the first of the match
     */
    public boolean isFirst() {
        return isFirst;
    }
}
