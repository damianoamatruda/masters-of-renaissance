package it.polimi.ingsw.common.events.mvevents;

/** Vatican section activation state update. */
public class UpdateVaticanSection implements MVEvent {
    /** The section's ID. */
    private final int vaticanSection;

    /**
     * Class constructor.
     *
     * @param vaticanSection the section's ID
     */
    public UpdateVaticanSection(int vaticanSection) {
        this.vaticanSection = vaticanSection;
    }

    /**
     * @return the section's ID
     */
    public int getVaticanSection() {
        return vaticanSection;
    }
}
