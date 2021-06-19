package it.polimi.ingsw.common.events.netevents;

public class ResHeartbeat implements NetEvent {
    @Override
    public boolean isPrioritized() {
        return true;
    }
}
