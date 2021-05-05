package it.polimi.ingsw.common.events;

import it.polimi.ingsw.common.ControllerObservable;

public class ReqActivateLeader implements VCEvent {
    private final int leader;

    public ReqActivateLeader(int leader) {
        this.leader = leader;
    }

    @Override
    public void handle(ControllerObservable view) {
        view.notify(this);
    }

    public int getLeader() {
        return leader;
    }
}
