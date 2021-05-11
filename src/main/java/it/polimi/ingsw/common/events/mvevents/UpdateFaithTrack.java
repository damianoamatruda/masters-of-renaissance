package it.polimi.ingsw.common.events.mvevents;

import it.polimi.ingsw.common.View;

/** Faith track state update. */
public class UpdateFaithTrack implements MVEvent {
    /** The player whose faith points increased. */
    private final String nickname;
    /** Whether the new position refers to the black cross marker. */
    private final boolean isBlackCross;
    /** The new marker position. */
    private final int position;

    /**
     * Class constructor.
     * 
     * @param nickname     the nickname of the player whose faith points increased
     * @param newPos       whether the new position refers to the black cross marker
     * @param isBlackCross the new marker position
     */
    public UpdateFaithTrack(String nickname, int newPos, boolean isBlackCross) {
        this.nickname = nickname;
        this.position = newPos;
        this.isBlackCross = isBlackCross;
    }

    /**
     * @return the nickname of the player whose faith points increased
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * @return the new marker position
     */
    public int getPosition() {
        return position;
    }

    /**
     * @return whether the new position refers to the black cross marker
     */
    public boolean isBlackCross() {
        return isBlackCross;
    }

    @Override
    public void handle(View view) {
        // TODO Auto-generated method stub
        
    }
}
