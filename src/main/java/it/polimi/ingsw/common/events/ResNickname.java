package it.polimi.ingsw.common.events;

import it.polimi.ingsw.common.ModelObserver;

public class ResNickname implements MVEvent {
    private final boolean isFirst;

    public ResNickname(boolean isFirst) {
        this.isFirst = isFirst;
    }

    @Override
    public void handle(ModelObserver view) {
        view.update(this);
    }

    public boolean isFirst() {
        return isFirst;
    }
}
