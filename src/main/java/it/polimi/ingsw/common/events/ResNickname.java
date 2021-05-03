package it.polimi.ingsw.common.events;

public class ResNickname implements MVEvent {
    private final boolean isFirst;

    public ResNickname(boolean isFirst) {
        this.isFirst = isFirst;
    }

    public boolean isFirst() {
        return isFirst;
    }
}
