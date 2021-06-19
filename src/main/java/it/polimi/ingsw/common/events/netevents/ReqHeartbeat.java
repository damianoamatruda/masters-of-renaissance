package it.polimi.ingsw.common.events.netevents;

public class ReqHeartbeat implements NetEvent {
    @Override
    public boolean isPrioritized() {
        return true;
    }
}
