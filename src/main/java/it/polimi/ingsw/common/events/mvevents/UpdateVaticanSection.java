package it.polimi.ingsw.common.events.mvevents;

import java.util.List;

/** Vatican section activation state update. */
public class UpdateVaticanSection implements MVEvent {
    /** The section's ID. */
    private final int vaticanSection;

    /** The list of players that earned a Pope's favor in this section. */
    private final List<String> bonusGivenPlayers;

    /**
     * Class constructor.
     *
     * @param vaticanSection the section's ID
     * @param bonusGivenPlayers the players that earned the bonus
     */
    public UpdateVaticanSection(int vaticanSection, List<String> bonusGivenPlayers) {
        this.vaticanSection = vaticanSection;
        this.bonusGivenPlayers = bonusGivenPlayers;
    }

    /**
     * @return the section's ID
     */
    public int getVaticanSection() {
        return vaticanSection;
    }

    /**
     * @return the players that earned the bonus
     */
    public List<String> getBonusGivenPlayers() {
        return bonusGivenPlayers;
    }
}
