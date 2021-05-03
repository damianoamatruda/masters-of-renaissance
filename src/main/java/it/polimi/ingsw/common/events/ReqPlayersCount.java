package it.polimi.ingsw.common.events;

import it.polimi.ingsw.common.View;

public class ReqPlayersCount implements VCEvent {
    private final int count;

    public ReqPlayersCount(int count) {
        this.count = count;
    }

    @Override
    public void handle(View view) {
        view.notify(this);
    }

    public int getCount() {
        return count;
    }
}
