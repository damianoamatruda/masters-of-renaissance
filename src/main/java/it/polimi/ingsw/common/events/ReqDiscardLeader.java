package it.polimi.ingsw.common.events;

import it.polimi.ingsw.common.View;

public class ReqDiscardLeader implements VCEvent {
    private final int leader;

    public ReqDiscardLeader(int leader) {
        this.leader = leader;
    }

    @Override
    public void handle(View view) {
        view.notify(this);
    }

    public int getLeader() {
        return leader;
    }
}
