package it.polimi.ingsw.common.events;

import it.polimi.ingsw.common.View;

/** Client request to discard a leader card. */
public class ReqDiscardLeader implements VCEvent {
    /** The ID of the leader card to be discareded. */
    private final int leader;

    /**
     * Class constructor.
     * 
     * @param leader the ID of the leader card to be discareded
     */
    public ReqDiscardLeader(int leader) {
        this.leader = leader;
    }

    @Override
    public void handle(View view) {
        view.notify(this);
    }

    /**
     * @return the ID of the leader card to be discareded
     */
    public int getLeader() {
        return leader;
    }
}
