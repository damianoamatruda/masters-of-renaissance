package it.polimi.ingsw.common.events;

import it.polimi.ingsw.common.View;

public class ResNickname implements MVEvent {
    private final boolean isFirst;

    /**
     * Class constructor.
     * 
     * @param isFirst whether the player joining is the first of the match
     */
    public ResNickname(boolean isFirst) {
        this.isFirst = isFirst;
    }

    @Override
    public void handle(View view) {
        view.update(this);
    }

    public boolean isFirst() {
        return isFirst;
    }
}
